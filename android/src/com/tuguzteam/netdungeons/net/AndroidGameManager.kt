package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.tuguzteam.netdungeons.MainActivity.Companion.auth
import com.tuguzteam.netdungeons.MainActivity.Companion.firestore
import com.tuguzteam.netdungeons.net.AndroidAuthManager.usersRef
import com.tuguzteam.netdungeons.net.FirebaseConstants.GAMES_COLLECTION
import com.tuguzteam.netdungeons.net.FirebaseConstants.GAME_PRIVATE_ADMIN_DOCUMENT
import com.tuguzteam.netdungeons.net.FirebaseConstants.GAME_PRIVATE_COLLECTION
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import ktx.async.KtxAsync
import kotlin.coroutines.coroutineContext

object AndroidGameManager : GameManager() {
    private val gamesRef by lazy { firestore.collection(GAMES_COLLECTION) }

    private var currentGameRef: DocumentReference? = null

    private var publicSnapshotListener: ListenerRegistration? = null
    private var privateSnapshotListener: ListenerRegistration? = null
    override var gameStateListener: ((GameState) -> Unit)? = null
        set(function) {
            val firebaseUser: FirebaseUser? = auth.currentUser
            val user = AndroidAuthManager.user
            val currentGameRef = currentGameRef
            val game = game
            if (currentGameRef != null && game != null && firebaseUser != null && user != null) {
                publicSnapshotListener?.remove()
                publicSnapshotListener = currentGameRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        publicSnapshotListener = null
                        field = null
                        function?.let {
                            it(GameState.Failure(cause = error))
                        }
                        return@addSnapshotListener
                    }
                    when {
                        // Error already was handled (based on documentation)
                        snapshot == null -> Unit
                        // Do not fire events on local changes
                        snapshot.metadata.hasPendingWrites() -> Unit
                        // Data does not exist (was deleted)
                        !snapshot.exists() -> {
                            this.game = null
                            this.currentGameRef = null
                            publicSnapshotListener?.remove()
                            publicSnapshotListener = null
                            field = null
                            function?.let {
                                it(GameState.Destroyed(game))
                            }
                        }
                        // Data exists - handle changes
                        else -> try {
                            val gameAdminReference = gameAdminReference(snapshot.reference)
                            KtxAsync.launch {
                                // Get data from the server about game seed
                                val seed = gameAdminReference.get().await().getLong(Game.SEED)

                                @Suppress("UNCHECKED_CAST")
                                val userIDs = snapshot[Game.USER_IDS] as? MutableList<String>?
                                checkNotNull(userIDs) { "No user IDs in game data" }
                                val serverGame = Game(userIDs, seed)
                                // Start game if needed
                                if (serverGame.seed != null && game.seed == null) {
                                    this@AndroidGameManager.game = Game(game.userIDs, serverGame.seed)
                                    function?.let {
                                        it(GameState.Started(game))
                                    }
                                }

                                val addedUserIDs = serverGame.userIDs.toMutableList().apply {
                                    removeAll(game.userIDs)
                                }
                                val removedUserIDs = game.userIDs.toMutableList().apply {
                                    removeAll(serverGame.userIDs)
                                }
                                this@AndroidGameManager.game = serverGame

                                // Handle added players' data
                                function?.let {
                                    val awaitList = mutableListOf<Deferred<User?>>()
                                    for (userID in addedUserIDs) awaitList += async {
                                        val newUserRef = usersRef.document(userID).get().await()
                                        newUserRef.toObject(User::class.java)
                                    }
                                    val newUsers = awaitList.awaitAll()
                                    for (newUser in newUsers) {
                                        newUser?.let {
                                            val player = Player(it.name, it.level)
                                            it(GameState.PlayerAdded(player))
                                        }
                                    }
                                }

                                // Handle removed players' data
                                function?.let {
                                    val awaitList = mutableListOf<Deferred<User?>>()
                                    for (userID in removedUserIDs) awaitList += async {
                                        val removedUserRef = usersRef.document(userID).get().await()
                                        removedUserRef.toObject(User::class.java)
                                    }
                                    val removedUsers = awaitList.awaitAll()
                                    for (newUser in removedUsers) {
                                        newUser?.let {
                                            val player = Player(it.name, it.level)
                                            it(GameState.PlayerRemoved(player))
                                        }
                                    }
                                }
                            }
                        } catch (exception: Exception) {
                            function?.let {
                                it(GameState.Failure(cause = exception))
                            }
                        }
                    }
                }
                val privateCollection = currentGameRef.collection(GAME_PRIVATE_COLLECTION)
                    .whereNotEqualTo("game-state-temporary", null)
                privateSnapshotListener?.remove()
                privateSnapshotListener = privateCollection.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        privateSnapshotListener = null
                        field = null
                        function?.let {
                            it(GameState.Failure(cause = error))
                        }
                        return@addSnapshotListener
                    }
                    when {
                        // Error already was handled (based on documentation)
                        snapshot == null -> Unit
                        // Do not fire events on local changes
                        snapshot.metadata.hasPendingWrites() -> Unit
                        // No data in collection
                        snapshot.isEmpty -> Unit
                        // Data exists in collection - handle changes
                        else -> {
                            for (document in snapshot.documentChanges) {
                                when (document.type) {
                                    DocumentChange.Type.ADDED -> TODO("Данные добавлены")
                                    DocumentChange.Type.MODIFIED -> TODO("Данные обновлены")
                                    DocumentChange.Type.REMOVED -> TODO("Данные удалены")
                                }
                            }
                        }
                    }
                }
            }
            field = function
        }

    private fun gameAdminReference(documentReference: DocumentReference) =
        documentReference.collection(GAME_PRIVATE_COLLECTION).document(GAME_PRIVATE_ADMIN_DOCUMENT)

    override suspend fun createGame() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        check(user != null && firebaseUser != null) { "User is not signed in" }
        check(currentGameRef == null && game == null) { "Game already created" }

        val userID = firebaseUser.uid
        val game = Game(mutableListOf(userID), null)
        val reference = gamesRef.document(userID)

        val userIDsMap = mapOf(Game.USER_IDS to game.userIDs)
        reference.set(userIDsMap).await()
        val seedMap = mapOf(Game.SEED to null)
        gameAdminReference(reference).set(seedMap).await()

        currentGameRef = gamesRef.document(userID)
        this.game = game
        game
    }

    override suspend fun insertIntoQueue() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val currentUser = AndroidAuthManager.user
        check(currentUser != null && firebaseUser != null) { "User is not signed in" }
        check(currentGameRef == null && game == null) { "Already in queue" }

        val allGames = gamesRef.get().await().documents
        val mutableList = mutableListOf<Deferred<Boolean>>()
        val scope = CoroutineScope(coroutineContext)
        allGames.forEach { game ->
            val gameAdminReference = gameAdminReference(gamesRef.document(game.id))
            val seed = gameAdminReference.get().await().getLong(Game.SEED)
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
        val updates = mapOf(Game.USER_IDS to FieldValue.arrayUnion(userID))
        currentGameRef.update(updates).await()

        @Suppress("UNCHECKED_CAST")
        val userIDs = suitableGame[Game.USER_IDS] as? MutableList<String>?
        checkNotNull(userIDs) { "No user IDs in game data" }
        val game = Game(userIDs)
        this.game = game
        this.currentGameRef = currentGameRef
        game
    }

    override suspend fun removeFromQueue() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        val currentGameRef = currentGameRef
        check(user != null && firebaseUser != null) { "User is not signed in" }
        check(currentGameRef != null && game != null) { "No current game to quit from" }

        val userID = firebaseUser.uid
        if (currentGameRef.id == userID) {
            val adminReference = gameAdminReference(currentGameRef).delete().asDeferred()
            val game = currentGameRef.delete().asDeferred()
            awaitAll(adminReference, game)
        } else {
            val updates = mapOf(Game.USER_IDS to FieldValue.arrayRemove(userID))
            currentGameRef.update(updates).await()
        }

        this.currentGameRef = null
        this.game = null
    }

    override suspend fun startGame(seed: Long) = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        val currentGameRef = currentGameRef
        var game = game
        check(user != null && firebaseUser != null) { "User is not signed in" }
        check(currentGameRef != null && game != null) { "Game was not created" }

        val adminReference = gameAdminReference(currentGameRef)
        val seedMap = mapOf(Game.SEED to seed)
        adminReference.set(seedMap).await()

        game = Game(game.userIDs, seed)
        this.game = game
        game
    }
}
