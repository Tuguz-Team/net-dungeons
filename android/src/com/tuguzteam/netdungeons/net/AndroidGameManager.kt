package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.tuguzteam.netdungeons.net.AndroidAuthManager.usersRef
import com.tuguzteam.netdungeons.net.Firebase.GAMES_COLLECTION
import com.tuguzteam.netdungeons.net.Firebase.GAME_PRIVATE_ADMIN_DOCUMENT
import com.tuguzteam.netdungeons.net.Firebase.GAME_PRIVATE_COLLECTION
import com.tuguzteam.netdungeons.net.Firebase.auth
import com.tuguzteam.netdungeons.net.Firebase.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

object AndroidGameManager : GameManager() {
    private val gamesRef by lazy { firestore.collection(GAMES_COLLECTION) }

    private var currentGameRef: DocumentReference? = null

    private fun gameAdminReference(documentReference: DocumentReference) =
        documentReference.collection(GAME_PRIVATE_COLLECTION).document(GAME_PRIVATE_ADMIN_DOCUMENT)

    override suspend fun createGame() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef == null && game == null) { "Game already created!" }

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
        val updates = mapOf(Game.USER_IDS to FieldValue.arrayUnion(userID))
        currentGameRef.update(updates).await()
        this.currentGameRef = currentGameRef
        @Suppress("UNCHECKED_CAST")
        this.game = Game(suitableGame[Game.USER_IDS] as MutableList<String>)
        game
    }

    override suspend fun removeFromQueue(): Result<Unit> = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        val currentGameRef = currentGameRef
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef != null && game != null) { "No current game to quit from!" }

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
        super.removeFromQueue()
    }

    override suspend fun startGame(seed: Long) = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val user = AndroidAuthManager.user
        val currentGameRef = currentGameRef
        val game = game
        check(user != null && firebaseUser != null) { "User is not signed in!" }
        check(currentGameRef != null && game != null) { "Game was not created!" }

        val adminReference = gameAdminReference(currentGameRef)
        val seedMap = mapOf(Game.SEED to seed)
        adminReference.set(seedMap).await()

        game.seed = seed
        game
    }
}
