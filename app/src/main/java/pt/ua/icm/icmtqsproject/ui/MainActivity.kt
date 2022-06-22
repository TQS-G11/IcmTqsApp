package pt.ua.icm.icmtqsproject.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.model.LoginRequest
import pt.ua.icm.icmtqsproject.data.model.Rider
import pt.ua.icm.icmtqsproject.ui.admin.view.AdminPage
import pt.ua.icm.icmtqsproject.ui.home.view.HomePage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Api stuff
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Check if logged in already or not
        val riderId: String? = sharedPreferences.getString("riderId", "")
        if (riderId != "") {
            // Go to admin page if logged in as admin
            if (riderId  == "admin@gmail.com") {
                val intent = Intent(this, AdminPage::class.java)
                startActivity(intent)
            }else{
                // Go to home page if logged in already
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            }
        }

        // Text Fields
        val emailField: EditText = findViewById(R.id.editEmailAddress)
        val passwordField: EditText = findViewById(R.id.editPassword)

        // Action buttons
        val signinButton: Button = findViewById(R.id.SignInButton)
        val registerButton: TextView = findViewById(R.id.registerText)

        // Buttons listeners
        signinButton.setOnClickListener {
            signin(emailField, passwordField, sharedPreferences)
        }

        registerButton.setOnClickListener {
            register()
        }

    }

    private fun register() {
        // Got to register page
        val intent = Intent(this, RegisterPage::class.java)
        startActivity(intent)
    }

    private fun signin(emailField: EditText, passwordField: EditText, prefs: SharedPreferences) {
        val email: String = emailField.text.toString()
        val password: String = passwordField.text.toString()

        if (email == "admin@gmail.com") {

            // Set on preferences
            val editor = prefs.edit()
            editor.putString("riderId", email)
            editor.apply()

            // Got to admin page
            val intent = Intent(this, AdminPage::class.java)
            startActivity(intent)
        } else {
            // Call login api endpoint
            if (email.isNotEmpty() && password.isNotEmpty()){
                // Create Rider
                val loginRequest: LoginRequest = LoginRequest(email,password)
                val json: String = Gson().toJson(loginRequest)

                // Call Api to get register
                val client = OkHttpClient()

                val mediaType = "application/json".toMediaTypeOrNull()
                val body = RequestBody.create(mediaType, json)
                val request = Request.Builder()
                    .url("http://213.199.129.9:8082/api/users/login")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                println("RESPONSE: " + response)
                if(response.code == 200){
                    // Get auth token
                    val token = ""

                    // Set on preferences
                    val editor = prefs.edit()
                    editor.putString("riderId", email)
                    editor.putString("authToken", token)
                    editor.apply()

                    // Got to right page
                    val intent = Intent(this, HomePage::class.java)

                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Wrong password", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Fill all fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}