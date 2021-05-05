package com.tuguzteam.netdungeons.net

import com.google.firebase.auth.FirebaseUser

class AndroidUser internal constructor(private val user: FirebaseUser) : User()
