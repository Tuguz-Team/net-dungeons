package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class AndroidNetworkManager : NetworkManager() {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val MATCHMAKING_QUEUE_REF = "matchmaking-queue"
    }

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val database = Firebase.database

    private val usersRef = firestore.collection(USERS_COLLECTION)
    private val matchmakingQueueRef = database.reference.child(MATCHMAKING_QUEUE_REF)

    override suspend fun updateUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(data = null)
        return try {
            val document = usersRef.document(firebaseUser.uid).get().await()
            user = document.toObject(User::class.java)
            Result.Success(data = user)
        } catch (e: CancellationException) {
            Result.Cancel()
        } catch (throwable: Throwable) {
            Result.Failure(cause = throwable)
        }
    }

    private suspend fun createUserFirestore(name: String, firebaseUser: FirebaseUser) {
        user = User(name, 0)
        usersRef.document(firebaseUser.uid).set(user!!).await()
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> =
        try {
            require(name matches NAME_REGEX) { "Name does not match pattern!" }
            require(email matches EMAIL_REGEX) { "Email does not match pattern!" }
            require(password matches PASSWORD_REGEX) { "Password does not match pattern!" }
            if (user != null) throw IllegalStateException("User is signed in!")

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!
            firebaseUser.updateProfile(userProfileChangeRequest {
                displayName = name
            }).await()
            createUserFirestore(name, firebaseUser)

            Result.Success(data = user!!)
        } catch (e: CancellationException) {
            Result.Cancel()
        } catch (throwable: Throwable) {
            Result.Failure(cause = throwable)
        }

    override suspend fun signIn(email: String, password: String): Result<User> =
        try {
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

    override suspend fun signOut(): Result<Unit> =
        if (user == null) {
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

    override suspend fun insertToMatchmakingQueue(): Result<Unit> =
        try {
            val user = this.user ?: throw IllegalStateException("User is not signed in!")
            matchmakingQueueRef.child(user.name).setValue(user.level).await()
            Result.Success(data = Unit)
        } catch (e: CancellationException) {
            Result.Cancel()
        } catch (throwable: Throwable) {
            Result.Failure(cause = throwable)
        }

    override suspend fun removeFromMatchmakingQueue(): Result<Unit> {
        TODO("Not yet implemented")
    }
}
