package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
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
            if (
                function != null && currentGameRef != null && game != null &&
                firebaseUser != null && user != null
            ) {
                publicSnapshotListener?.remove()
                publicSnapshotListener = currentGameRef.addSnapshotListener { snapshot, error ->
                    error?.let {
                        publicSnapshotListener = null
                        field = null
                        function(GameState.Failure(cause = it))
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
                            function(GameState.Destroyed(game))
                        }
                        // Data exists - handle changes
                        else -> try {
                            val gameAdminReference = gameAdminReference(snapshot.reference)
                            KtxAsync.launch {
                                // Get data from the server about game seed
                                val seed = gameAdminReference.get().await().getLong(Game::seed.name)

                                @Suppress("UNCHECKED_CAST")
                                val userIDs = snapshot[Game::userIDs.name] as? MutableList<String>?
                                checkNotNull(userIDs) { "No user IDs in game data" }
                                val serverGame = Game(userIDs, seed)
                                // Start game if needed
                                if (serverGame.seed != null && game.seed == null) {
                                    this@AndroidGameManager.game =
                                        Game(game.userIDs, serverGame.seed)
                                    function(GameState.Started(game))
                                }

                                this@AndroidGameManager.game = serverGame

                                // Get added players' data
                                val addedUserIDs = serverGame.userIDs - game.userIDs
                                val addedUsers = addedUserIDs.map { userID ->
                                    async {
                                        val addedUserRef = usersRef.document(userID).get().await()
                                        addedUserRef.toObject(User::class.java)
                                    }
                                }.awaitAll()
                                // Send information about added players
                                addedUsers.asSequence().filterNotNull().forEach {
                                    val player = Player(it.name, it.level)
                                    function(GameState.PlayerAdded(player))
                                }

                                // Get removed players' data
                                val removedUserIDs = game.userIDs - serverGame.userIDs
                                val removedUsers = removedUserIDs.map { userID ->
                                    async {
                                        val removedUserRef = usersRef.document(userID).get().await()
                                        removedUserRef.toObject(User::class.java)
                                    }
                                }.awaitAll()
                                // Send information about removed players
                                removedUsers.asSequence().filterNotNull().forEach {
                                    val player = Player(it.name, it.level)
                                    function(GameState.PlayerRemoved(player))
                                }
                            }
                        } catch (exception: Exception) {
                            function(GameState.Failure(cause = exception))
                        }
                    }
                }
                val privateCollection = currentGameRef.collection(GAME_PRIVATE_COLLECTION)
                    .whereNotEqualTo("game-state-temporary", null)
                privateSnapshotListener?.remove()
                privateSnapshotListener = privateCollection.addSnapshotListener { snapshot, error ->
                    error?.let {
                        privateSnapshotListener = null
                        field = null
                        function(GameState.Failure(cause = it))
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

        val userIDsMap = mapOf(Game::userIDs.name to game.userIDs)
        reference.set(userIDsMap).await()
        val seedMap = mapOf(Game::seed.name to null)
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

        val allGames: List<DocumentSnapshot> = gamesRef.get().await().documents
        val scope = CoroutineScope(coroutineContext)
        val allGamesIsSuitable = allGames.map { game ->
            scope.async {
                val gameAdminReference = gameAdminReference(gamesRef.document(game.id))
                val seed = gameAdminReference.get().await().getLong(Game::seed.name)
                seed?.let { return@async false }
                val document = usersRef.document(game.id).get().await()
                val user = document.toObject(User::class.java)
                val threshold = 2
                user?.let {
                    it.level in (currentUser.level - threshold)..(currentUser.level + threshold)
                } == true
            }
        }.awaitAll()

        val index = allGamesIsSuitable.indexOf(true)
        val suitableGame = allGames[index]
        val userID = firebaseUser.uid
        val currentGameRef = suitableGame.reference
        val updates = mapOf(Game::userIDs.name to FieldValue.arrayUnion(userID))
        currentGameRef.update(updates).await()

        @Suppress("UNCHECKED_CAST")
        val userIDs = suitableGame[Game::userIDs.name] as? MutableList<String>?
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
            val updates = mapOf(Game::userIDs.name to FieldValue.arrayRemove(userID))
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
        val seedMap = mapOf(Game::seed.name to seed)
        adminReference.set(seedMap).await()

        game = Game(game.userIDs, seed)
        this.game = game
        game
    }
}
