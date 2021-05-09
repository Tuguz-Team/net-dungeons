package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.OnDisconnect
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import ktx.log.error
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

class AndroidNetworkManager : NetworkManager() {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val GAMES_COLLECTION = "games"
    }

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val database = Firebase.database

    private val usersRef = firestore.collection(USERS_COLLECTION)
    private val gamesRef = firestore.collection(GAMES_COLLECTION)

    private var currentGameRef: DocumentReference? = null

    private var onDisconnect: OnDisconnect? = null
    private val onDisconnectCompletionListener = DatabaseReference.CompletionListener { error, _ ->
        error?.let {
            logger.error(it.toException()) {
                "Could not establish disconnect event: ${it.message}"
            }
        }
    }

    private fun gameAdminReference(documentReference: DocumentReference) =
        documentReference.collection("private").document("admin")

    private suspend fun setupOnDisconnectRef(firebaseUser: FirebaseUser) {
        val ref = database.reference.child("online-users").child(firebaseUser.uid)
        ref.setValue(true).await()
        onDisconnect?.cancel()
        onDisconnect = ref.onDisconnect().apply {
            setValue(false, onDisconnectCompletionListener)
        }
    }

    override suspend fun updateUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(data = null)
        return resultFrom {
            setupOnDisconnectRef(firebaseUser)
            val document = usersRef.document(firebaseUser.uid).get().await()
            user = document.toObject(User::class.java)
            user
        }
    }

    override suspend fun register(name: String, email: String, password: String) = resultFrom {
        require(name matches NAME_REGEX) { "Name does not match pattern!" }
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
        check(user == null) { "User is signed in!" }

        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser: FirebaseUser? = authResult.user
        check(firebaseUser != null) { "WTF user is null?" }
        firebaseUser.updateProfile(userProfileChangeRequest {
            displayName = name
        }).await()

        setupOnDisconnectRef(firebaseUser)
        val user = User(name, 0)
        usersRef.document(firebaseUser.uid).set(user).await()
        this.user = user
        user
    }

    override suspend fun signIn(email: String, password: String) = try {
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
        check(user == null) { "User is signed in!" }

        auth.signInWithEmailAndPassword(email, password).await()
        val result = this.updateUser()
        if (result is Result.Success) {
            val user = result.data
            check(user != null) { "WTF user is null?" }
            Result.Success(data = user)
        } else {
            @Suppress("UNCHECKED_CAST")
            result as Result<User>
        }
    } catch (e: CancellationException) {
        Result.Cancel()
    } catch (throwable: Throwable) {
        Result.Failure(cause = throwable)
    }

    override suspend fun signOut(): Result<Unit> = resultFrom {
        check(user != null) { "User is not signed in!" }
        auth.signOut()
        suspendCancellableCoroutine<Unit> { cont ->
            var listener: FirebaseAuth.AuthStateListener? = null
            val authStateListener = FirebaseAuth.AuthStateListener {
                val user: FirebaseUser? = it.currentUser
                if (user == null) {
                    auth.removeAuthStateListener(listener!!)
                    cont.resume(Unit)
                }
            }
            listener = authStateListener
            auth.addAuthStateListener(authStateListener)
        }
        super.signOut()
    }

    override suspend fun createGame() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef == null && game == null) { "Game already created!" }

        val userID = firebaseUser.uid
        val game = Game(mutableListOf(userID), null)
        val reference = gamesRef.document(userID)
        val userIDsMap = mapOf("userIDs" to game.userIDs)
        reference.set(userIDsMap).await()
        val seedMap = mapOf("seed" to null)
        gameAdminReference(reference).set(seedMap).await()

        currentGameRef = gamesRef.document(userID)
        this.game = game
        game
    }

    override suspend fun insertIntoQueue() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val currentUser = user
        check(currentUser != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef == null && game == null) { "Already in queue!" }

        val allGames = gamesRef.get().await().documents
        val mutableList = mutableListOf<Deferred<Boolean>>()
        val scope = CoroutineScope(coroutineContext)
        allGames.forEach { game ->
            val seed = gameAdminReference(gamesRef.document(game.id)).get().await()["seed"] as Long?
            if (seed != null) return@forEach
            mutableList += scope.async {
                val document = usersRef.document(game.id).get().await()
                val user = document.toObject(User::class.java)
                val threshold = 2
                user?.let {
                    it.level in (currentUser.level - threshold)..(currentUser.level + threshold)
                } == true
            }
        }

        val index = mutableList.awaitAll().indexOf(true)
        val suitableGame = allGames[index]
        val userID = firebaseUser.uid
        val currentGameRef = suitableGame.reference
        val updates = mapOf("userIDs" to FieldValue.arrayUnion(userID))
        currentGameRef.update(updates).await()
        this.currentGameRef = currentGameRef
        @Suppress("UNCHECKED_CAST")
        this.game = Game(suitableGame["userIDs"] as MutableList<String>)
        game
    }

    override suspend fun removeFromQueue() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val currentGameRef = currentGameRef
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef != null && game != null) { "No current game to quit from!" }

        val userID = firebaseUser.uid
        if (currentGameRef.id == userID) {
            val adminReference = gameAdminReference(currentGameRef).delete().asDeferred()
            val game = currentGameRef.delete().asDeferred()
            awaitAll(adminReference, game)
        } else {
            val updates = mapOf("userIDs" to FieldValue.arrayRemove(userID))
            currentGameRef.update(updates).await()
        }
        this.currentGameRef = null
        this.game = null
    }

    override suspend fun startGame(seed: Long) = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val currentGameRef = currentGameRef
        val game = game
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef != null && game != null) { "Game was not created!" }

        val adminReference = gameAdminReference(currentGameRef)
        val seedMap = mapOf("seed" to seed)
        adminReference.set(seedMap).await()

        game.seed = seed
        game
    }
}
