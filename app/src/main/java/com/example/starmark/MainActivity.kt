package com.example.starmark

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            updateUI(auth.currentUser!!.uid)
        }

        registerButton.setOnClickListener {
            performRegistration()
        }

        alreadyHaveAccountTextView.setOnClickListener {
            Log.d("MainActivity", "Trying to change to login")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(uid: String) {
        val intent = Intent(this, HomeScreenActivity::class.java)
//        intent.putExtra("userId", id)
        intent.putExtra("userId", uid)
        startActivity(intent)
    }

    private fun performRegistration(){
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        Log.d("MainActivity", "Email is $email")
        Log.d("MainActivity", "Username is $username")

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email/password", Toast.LENGTH_SHORT).show()
            return
        }

        // Use FirebaseAuth to create user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

                // its successful
                var db = FirebaseFirestore.getInstance()
                val user = HashMap<String, Any>()
                id = it.result?.user?.uid!!.toString()
                user["id"] = id
                user["email"] = email
                user["words"] = mutableListOf<Int>()
                db.collection("users").document(id).set(user)

//                ref.child(id!!).setValue(user)

                Log.d("MainActivity", "Successfully created user with id" + it.result?.user?.uid)
                Toast.makeText(this, "You are registered.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Failed to create user. Message: ${it.message}")
                Toast.makeText(this, "Failed to create user. Message: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
