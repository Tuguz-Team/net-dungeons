package com.tuguzteam.netdungeons.net

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ktx.log.debug
import ktx.log.error

class AndroidNetworkManager : NetworkManager() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    init {
        auth.currentUser?.let {
            user = User(it.displayName, null)
        }
    }

    private fun authOnCompleteListener(
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) = OnCompleteListener<AuthResult> { task ->
        if (task.isSuccessful) {
            var listener: FirebaseAuth.AuthStateListener? = null
            val authStateListener = FirebaseAuth.AuthStateListener {
                val user: FirebaseUser? = it.currentUser
                if (user != null) {
                    logger.debug { "Registration succeed!" }
                    onSuccess(user)
                }
                auth.removeAuthStateListener(listener!!)
            }
            listener = authStateListener
            auth.addAuthStateListener(authStateListener)
        } else {
            task.exception?.let { e ->
                logger.error(e) { "Registration failed!" }
                onFailure(e)
            }
        }
    }

    private fun addUserData(name: String, firebaseUser: FirebaseUser, callback: Callback) {
        val userData = hashMapOf(
            "name" to name,
            "uid" to firebaseUser.uid,
            "level" to 0,
        )
        firestore.collection("users").add(userData)
            .addOnSuccessListener {
                logger.debug { "User data added to Firestore!" }
                user = User(name, 0)
                callback.onSuccess(user!!)
            }.addOnFailureListener { e ->
                logger.error(e) { "Firestore user data write failed!" }
                callback.onFailure(e)
            }
    }

    override fun register(email: String, password: String, name: String, callback: Callback) {
        super.register(email, password, name, callback)
        if (user == null) {
            val listener = authOnCompleteListener(
                onSuccess = { firebaseUser ->
                    firebaseUser.updateProfile(userProfileChangeRequest {
                        displayName = name
                    }).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            addUserData(name, firebaseUser, callback)
                        } else task.exception?.let { e ->
                            logger.error(e) { "Registration failed!" }
                            callback.onFailure(e)
                        }
                    }
                },
                onFailure = callback.onFailure
            )
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener)
        }
    }

    override fun signIn(email: String, password: String, callback: Callback) {
        super.signIn(email, password, callback)
        if (user == null) {
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(authOnCompleteListener(callback))
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
