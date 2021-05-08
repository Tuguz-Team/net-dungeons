package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

class AndroidNetworkManager : NetworkManager() {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val GAMES_COLLECTION = "games"
    }

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val usersRef = firestore.collection(USERS_COLLECTION)
    private val gamesRef = firestore.collection(GAMES_COLLECTION)

    override suspend fun updateUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(data = null)
        return resultFrom {
            val document = usersRef.document(firebaseUser.uid).get().await()
            user = document.toObject(User::class.java)
            user
        }
    }

    override suspend fun register(name: String, email: String, password: String) = resultFrom {
        require(name matches NAME_REGEX) { "Name does not match pattern!" }
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
        if (user != null) throw IllegalStateException("User is signed in!")

        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user!!
        firebaseUser.updateProfile(userProfileChangeRequest {
            displayName = name
        }).await()
        user = User(name, 0)
        usersRef.document(firebaseUser.uid).set(user!!).await()
        user!!
    }

    override suspend fun signIn(email: String, password: String) = try {
        require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
        if (user != null) throw IllegalStateException("User is signed in!")

        auth.signInWithEmailAndPassword(email, password).await()
        val result = this.updateUser()
        if (result is Result.Success) {
            val data = result.data ?: throw IllegalStateException("WTF user is null?")
            Result.Success(data)
        } else {
            @Suppress("UNCHECKED_CAST")
            result as Result<User>
        }
    } catch (e: CancellationException) {
        Result.Cancel()
    } catch (throwable: Throwable) {
        Result.Failure(cause = throwable)
    }

    override suspend fun signOut() = if (user == null) {
        Result.Failure(cause = IllegalStateException("User is not signed in!"))
    } else {
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

    private var currentGameRef: DocumentReference? = null

    override suspend fun createRoom() = resultFrom {
        val exception by lazy {
            IllegalStateException("User is not signed in!")
        }
        this.user ?: throw exception
        val firebaseUser = auth.currentUser ?: throw exception
        check(this.currentGameRef == null) { "Room already created!" }

        val userID = firebaseUser.uid
        val game = Game(mutableListOf(userID), null)
        try {
            gamesRef.document(userID).set(game).await()
        } catch (e: FirebaseFirestoreException) {
            if (e.code != FirebaseFirestoreException.Code.PERMISSION_DENIED) throw e
        }
        currentGameRef = gamesRef.document(userID)
        this.game = game
        game
    }

    override suspend fun insertIntoQueue() = resultFrom {
        val exception by lazy {
            IllegalStateException("User is not signed in!")
        }
        this.user ?: throw exception
        val firebaseUser = auth.currentUser ?: throw exception
        check(this.currentGameRef == null) { "Already in queue!" }

        val allGames = gamesRef.whereEqualTo("seed", null).get().await().documents
        val mutableList = mutableListOf<Deferred<Boolean>>()
        val scope = CoroutineScope(coroutineContext)
        allGames.forEach { game ->
            mutableList += scope.async {
                val document = usersRef.document(game.id).get().await()
                val user = document.toObject(User::class.java)
                user?.let { it.level in 0..3 } == true
            }
        }

        val index = mutableList.awaitAll().indexOf(true)
        val suitableGame = allGames[index]
        val userID = firebaseUser.uid
        val currentGameRef = suitableGame.reference
        val updates = mapOf(
            "userIDs" to FieldValue.arrayUnion(userID)
        )
        currentGameRef.update(updates).await()
        this.currentGameRef = currentGameRef
        this.game = suitableGame.toObject(Game::class.java)
        game
    }

    override suspend fun removeFromQueue() = resultFrom {
        val exception by lazy {
            IllegalStateException("User is not signed in!")
        }
        user ?: throw exception
        val firebaseUser = auth.currentUser ?: throw exception
        val currentGameRef = currentGameRef
            ?: throw IllegalStateException("No current game to quit from!")

        val userID = firebaseUser.uid
        if (currentGameRef.id == userID) {
            currentGameRef.delete().await()
        } else {
            val updates = mapOf(
                "userIDs" to FieldValue.arrayRemove(userID)
            )
            currentGameRef.update(updates).await()
        }
        this.currentGameRef = null
        this.game = null
    }
}
