package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ktx.log.error

class AndroidNetworkManager : NetworkManager() {
    private val auth = Firebase.auth

    init {
        setUser(auth.currentUser)
    }

    private fun setUser(firebaseUser: FirebaseUser?) {
        firebaseUser?.let {
            user = AndroidUser(it)
        }
    }

    override fun register(email: String, password: String, name: String, onCompleted: () -> Unit) {
        if (user != null) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setUser(auth.currentUser)
                    user?.let { it.name = name }
                } else {
                    task.exception?.let {
                        logger.error(it) { "Registration failed!" }
                    }
                }
            }
        }
        onCompleted()
    }

    override fun signIn(email: String, password: String, onCompleted: () -> Unit) {
        if (user == null) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    setUser(auth.currentUser)
                } else {
                    task.exception?.let {
                        logger.error(it) { "Sign in failed!" }
                    }
                }
            }
        }
        onCompleted()
    }

    override fun signOut() = auth.signOut()
}
