package pt.ua.icm.icmtqsproject.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import pt.ua.icm.icmtqsproject.R
import pt.ua.icm.icmtqsproject.data.model.Rider
import pt.ua.icm.icmtqsproject.ui.home.view.HomePage


class RegisterPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        // Api stuff
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Shared Preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Text Fields
        val nameField: EditText = findViewById(R.id.editName)
        val emailField: EditText = findViewById(R.id.editEmailAddress)
        val cidField: EditText = findViewById(R.id.editCID)
        val passwordField: EditText = findViewById(R.id.editPassword)

        // Action buttons
        val registerButton: Button = findViewById(R.id.RegisterButton)

        // Buttons listeners
        registerButton.setOnClickListener {
            register(nameField, emailField, passwordField, cidField, sharedPreferences)
        }
    }

    private fun register(nameField: EditText, emailField: EditText, passwordField: EditText, cidField: EditText, prefs: SharedPreferences) {
        val email: String = emailField.text.toString()
        val password: String = passwordField.text.toString()
        val name: String = nameField.text.toString()

        // Call register api endpoint
        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()){
            // Create Rider

            val rider: Rider = Rider(email,password,name)
            val json: String = Gson().toJson(rider)

            // Call Api to get register
            val client = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, json)
            val request = Request.Builder()
                .url("http://213.199.129.9:8082/api/users/signup")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            // println("RESPONSE: " + response)
            if(response.code == 201){
                // Get auth token
                val token = ""

                // Set on preferences
                val editor = prefs.edit()
                editor.putString("riderId", email)
                editor.putString("authToken", token)
                editor.apply()

                // Got to main page
                val intent = Intent(this, HomePage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(applicationContext, "Email already in use", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "Fill all fields", Toast.LENGTH_LONG).show()
        }
    }
}