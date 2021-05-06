package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class AndroidNetworkManager : NetworkManager() {
    companion object {
        private const val USERS_COLLECTION = "users"

        private const val NAME_FIELD = "name"
        private const val LEVEL_FIELD = "level"
    }

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val usersRef = firestore.collection(USERS_COLLECTION)

    override suspend fun updateUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(data = null)
        return try {
            val document = usersRef.document(firebaseUser.uid).get().await()
            user = User(document[NAME_FIELD] as String?, document[LEVEL_FIELD] as Long?)
            Result.Success(data = user)
        } catch (e: CancellationException) {
            Result.Cancel()
        } catch (throwable: Throwable) {
            Result.Failure(cause = throwable)
        }
    }

    private suspend fun createUserFirestore(name: String, firebaseUser: FirebaseUser) {
        val userData = hashMapOf(
            NAME_FIELD to name,
            LEVEL_FIELD to 0,
        )
        usersRef.document(firebaseUser.uid).set(userData).await()
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

            user = User(name, 0)
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

    override suspend fun signOut() {
        if (user != null) {
            auth.signOut()
        }
        super.signOut()
    }
}
