package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AndroidNetworkManager : NetworkManager() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private val usersRef = firestore.collection("users")

    override suspend fun updateUser() {
        val query = auth.currentUser?.let {
            usersRef.whereEqualTo("uid", it.uid).get().await()
        }
        val document = query?.documents?.get(0)
        document?.let {
            user = User(document["name"] as String?, document["level"] as Long?)
        }
    }

    private suspend fun createUserFirestore(name: String, firebaseUser: FirebaseUser) {
        val userData = hashMapOf(
            "name" to name,
            "uid" to firebaseUser.uid,
            "level" to 0,
        )
        usersRef.add(userData).await()
    }

    override suspend fun register(name: String, email: String, password: String) {
        super.register(name, email, password)
        if (user == null) {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult?.user?.let {
                it.updateProfile(userProfileChangeRequest {
                    displayName = name
                }).await()
                createUserFirestore(name, it)
                this.user = User(name, 0)
            }
            return
        }
        throw IllegalStateException("User is signed in!")
    }

    override suspend fun signIn(email: String, password: String) {
        super.signIn(email, password)
        if (user == null) {
            auth.signInWithEmailAndPassword(email, password).await()
            updateUser()
            return
        }
        throw IllegalStateException("User is already signed in!")
    }

    override suspend fun signOut() {
        if (user != null) {
            auth.signOut()
        }
        super.signOut()
    }
}
