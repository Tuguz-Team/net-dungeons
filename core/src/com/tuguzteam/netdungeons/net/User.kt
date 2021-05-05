package com.tuguzteam.netdungeons.net

abstract class User {
    var name: String = ""
    var level: UInt = 0u

    override fun toString() = """User("$name", $level)"""
}
