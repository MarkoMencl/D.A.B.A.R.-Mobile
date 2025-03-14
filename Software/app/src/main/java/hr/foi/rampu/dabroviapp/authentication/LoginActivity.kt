package hr.foi.rampu.dabroviapp.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hr.foi.rampu.dabroviapp.MainActivity
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.helpers.SessionManager

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin: Button = findViewById(R.id.btnConfirmRegistration)

        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser()
    {
        val username = findViewById<EditText>(R.id.edtLoginUsername).text.toString()
        val password = findViewById<EditText>(R.id.edtPassword).text.toString()

        if(username == "" || password == "")
        {
            val toastMessage = getString(R.string.toastSignupEmptyError)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            return
        }

        SessionManager.login(username, password, this) { success ->
            runOnUiThread {
                if(success){
                    val user = SessionManager.getUser()
                    Log.e("LocaleManager", "$user")
                    val userLanguage = user?.language ?: "hr"
                    Log.e("LocaleManager", "$userLanguage")
                    LocaleManager.setLocaleAndRecreateActivity(this@LoginActivity, userLanguage)
                    endLoginActivity()
                }else{
                    Toast.makeText(this, R.string.toastInvalidLoginError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun endLoginActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}
