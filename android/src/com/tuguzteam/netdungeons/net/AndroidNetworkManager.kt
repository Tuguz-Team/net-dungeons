package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ktx.log.error

class AndroidNetworkManager : NetworkManager {
    private val auth = Firebase.auth
    private var user: FirebaseUser? = auth.currentUser

    override fun auth(onAuth: (User?) -> Unit) {
        if (user == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                } else {
                    task.exception?.let {
                        NetworkManager.logger.error(it) { "Error on user authentication" }
                    }
                }
            }
        }
        onAuth(user?.let { AndroidUser(it) })
    }
}
