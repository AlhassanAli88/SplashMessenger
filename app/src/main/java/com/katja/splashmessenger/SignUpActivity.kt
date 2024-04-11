package com.katja.splashmessenger
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.katja.splashmessenger.databinding.ActivitySignUpBinding
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        userDao = UserDao()

        binding.signUpButton.setOnClickListener {
            registerUser()
        }

        binding.goToLogInTextClick.setOnClickListener {
            // Start LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.passwordConfirmEditText.text.toString()
        val username = binding.fullNameEditText.text.toString()

        if (password != confirmPassword) {
            Toast.makeText(this, "Password need to match", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val user = auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val newUser = createUser(username, email, password)
                    userDao.addUser(newUser)

                    Toast.makeText(this, "Welcome: ${user?.displayName ?: user?.email}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, UserConversationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to sign up", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to sign up: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createUser(username: String, email: String, password: String): User {
        val user = User(UUID.randomUUID().toString(), username, email, password)
        return user
    }
}
