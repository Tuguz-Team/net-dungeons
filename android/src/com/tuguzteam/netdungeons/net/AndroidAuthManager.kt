package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.OnDisconnect
import com.tuguzteam.netdungeons.net.Firebase.USERS_COLLECTION
import com.tuguzteam.netdungeons.net.Firebase.auth
import com.tuguzteam.netdungeons.net.Firebase.database
import com.tuguzteam.netdungeons.net.Firebase.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import ktx.log.error
import kotlin.coroutines.resume

object AndroidAuthManager : AuthManager() {
    val usersRef by lazy { firestore.collection(USERS_COLLECTION) }

    private var onDisconnect: OnDisconnect? = null
    private val onDisconnectCompletionListener = DatabaseReference.CompletionListener { error, _ ->
        error?.let {
            logger.error(it.toException()) {
                "Could not establish disconnect event: ${it.message}"
            }
        }
    }

    private suspend fun setupOnDisconnectRef(firebaseUser: FirebaseUser) {
        val ref = database.reference
            .child(Firebase.USERS_ONLINE_CHILD)
            .child(firebaseUser.uid)
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

    override suspend fun signIn(email: String, password: String) = try {
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        check(user == null) { "User is signed in" }

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

    override suspend fun register(name: String, email: String, password: String) = resultFrom {
        require(name matches NAME_REGEX) { "Name does not match pattern" }
        require(email matches EMAIL_REGEX) { "Email does not match pattern" }
        require(password matches PASSWORD_REGEX) { "Password does not match pattern" }
        check(user == null) { "User is signed in" }

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

    override suspend fun signOut(): Result<Unit> = resultFrom {
        checkNotNull(user) { "User is not signed in" }
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
}
