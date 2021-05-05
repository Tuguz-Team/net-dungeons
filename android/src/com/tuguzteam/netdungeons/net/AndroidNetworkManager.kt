package com.tuguzteam.netdungeons.net

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ktx.log.error

class AndroidNetworkManager : NetworkManager() {
    private val auth = Firebase.auth

    init {
        auth.currentUser?.let {
            user = AndroidUser(it)
        }
    }

    private fun authOnCompleteListener(callback: Callback) =
        OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                var listener: FirebaseAuth.AuthStateListener? = null
                val authStateListener = FirebaseAuth.AuthStateListener {
                    val user: FirebaseUser? = it.currentUser
                    if (user != null) {
                        this.user = AndroidUser(user)
                        callback.onSuccess(this.user as AndroidUser)
                    }
                    auth.removeAuthStateListener(listener!!)
                }
                listener = authStateListener
                auth.addAuthStateListener(authStateListener)
            } else {
                task.exception?.let {
                    logger.error(it) { "Registration failed!" }
                    callback.onFailure(it)
                }
            }
        }

    override fun register(email: String, password: String, name: String, callback: Callback) {
        super.register(email, password, name, callback)
        if (user == null) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(authOnCompleteListener(callback))
        }
    }

    override fun signIn(email: String, password: String, callback: Callback) {
        super.signIn(email, password, callback)
        if (user == null) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authOnCompleteListener(callback))
        }
    }

    override fun signOut(onSuccess: () -> Unit) {
        auth.signOut()
        var listener: FirebaseAuth.AuthStateListener? = null
        val authStateListener = FirebaseAuth.AuthStateListener {
            val user: FirebaseUser? = it.currentUser
            if (user == null) {
                this.user = null
                onSuccess()
            }
            auth.removeAuthStateListener(listener!!)
        }
        listener = authStateListener
        auth.addAuthStateListener(authStateListener)
    }
}
