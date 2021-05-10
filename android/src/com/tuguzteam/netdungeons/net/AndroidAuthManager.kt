package com.tuguzteam.netdungeons.net

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.tuguzteam.netdungeons.*
import com.tuguzteam.netdungeons.MainActivity.Companion.auth
import com.tuguzteam.netdungeons.MainActivity.Companion.database
import com.tuguzteam.netdungeons.MainActivity.Companion.firestore
import com.tuguzteam.netdungeons.net.FirebaseConstants.USERS_COLLECTION
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import ktx.async.KtxAsync
import ktx.log.error
import kotlin.coroutines.resume

object AndroidAuthManager : AuthManager() {
    val usersRef by lazy { firestore.collection(USERS_COLLECTION) }

    private suspend fun setupOnDisconnectRef(firebaseUser: FirebaseUser) {
        val userRef = database.reference
            .child(FirebaseConstants.USERS_ONLINE_CHILD)
            .child(firebaseUser.uid)
        database.reference.child(".info/connected").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == false) return
                    KtxAsync.launch {
                        userRef.onDisconnect().setValue(false).await()
                        userRef.setValue(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    val exception = error.toException()
                    logger.error(cause = exception) { "Listening for .info/connected cancelled" }
                }
            }
        )
    }

    override suspend fun syncUser(): Result<User?> {
        val firebaseUser = auth.currentUser ?: return Result.Success(data = null)
        return resultFrom {
            setupOnDisconnectRef(firebaseUser)
            val document = usersRef.document(firebaseUser.uid).get().await()
            user = document.toObject(User::class.java)
            user
        }
    }

    override suspend fun signIn(email: String, password: String) = try {
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        check(user == null) { "User is signed in" }

        // Throw exception based on Firebase exception type
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthInvalidCredentialsException -> AuthInvalidPasswordException()
                is FirebaseAuthInvalidUserException -> AuthInvalidUserException()
                is FirebaseNetworkException -> WeakNetworkException()
                else -> e
            }
        }

        when (val result = this.syncUser()) {
            is Result.Cancel -> Result.Cancel()
            is Result.Failure -> Result.Failure(cause = result.cause)
            is Result.Success -> {
                val user = result.data
                check(user != null) { "WTF user is null?" }
                Result.Success(data = user)
            }
        }
    } catch (e: CancellationException) {
        Result.Cancel()
    } catch (throwable: Throwable) {
        Result.Failure(cause = throwable)
    }

    override suspend fun register(name: String, email: String, password: String) = resultFrom {
        require(name matches NAME_REGEX) { "Name does not match pattern" }
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        check(user == null) { "User is signed in" }

        val authResult: AuthResult
        try {
            authResult = auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw when (e) {
                is FirebaseAuthUserCollisionException -> AuthUserCollisionException()
                is FirebaseAuthWeakPasswordException -> AuthInvalidPasswordException()
                is FirebaseAuthInvalidCredentialsException -> AuthInvalidEmailException()
                is FirebaseNetworkException -> WeakNetworkException()
                else -> e
            }
        }

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

    override suspend fun signOut() = resultFrom {
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

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
        this.user = null
    }

    override suspend fun updateName(name: String) = resultFrom {
        require(name matches NAME_REGEX) { "Name does not match pattern" }
        var user = user
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

        val updateProfile = firebaseUser.updateProfile(userProfileChangeRequest {
            displayName = name
        }).asDeferred()
        user = User(name, user.level)
        val updateFirestore = usersRef.document(firebaseUser.uid).set(user).asDeferred()
        awaitAll(updateProfile, updateFirestore)

        this.user = user
        user
    }

    override suspend fun updateEmail(email: String) = resultFrom {
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        val user = user
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

        firebaseUser.updateEmail(email).await()
        user
    }

    override suspend fun updatePassword(password: String) = resultFrom {
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        val user = user
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

        firebaseUser.updatePassword(password).await()
        user
    }

    override suspend fun deleteUser() = resultFrom {
        val user = user
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

        val firestoreDelete = usersRef.document(firebaseUser.uid).delete().asDeferred()
        val authDelete = firebaseUser.delete().asDeferred()
        awaitAll(firestoreDelete, authDelete)
        this.user = null
    }

    override suspend fun reAuth(email: String, password: String): Result<Unit> = resultFrom {
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        val user = user
        val firebaseUser: FirebaseUser? = auth.currentUser
        check(user != null && firebaseUser != null) { "User is not signed in" }

        val credential = EmailAuthProvider.getCredential(email, password)
        firebaseUser.reauthenticate(credential).await()
    }
}
