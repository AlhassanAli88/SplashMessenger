package com.katja.splashmessenger

class User(var id: String, val fullName: String, val email: String, val password: String) {

    override fun toString(): String {
        return "User('id = $id', 'fullName = $fullName', 'email = $email', 'password = $password')"
    }
}

