package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Firebase {
    const val USERS_COLLECTION = "users"
    const val GAMES_COLLECTION = "games"
    const val GAME_PRIVATE_COLLECTION = "private"
    const val GAME_PRIVATE_ADMIN_DOCUMENT = "admin"
    const val USERS_ONLINE_CHILD = "users-online"

    val auth by lazy { Firebase.auth }
    val firestore by lazy { Firebase.firestore }
    val database by lazy { Firebase.database }
}
