package hr.foi.rampu.dabroviapp.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hr.foi.rampu.dabroviapp.MainActivity
import hr.foi.rampu.dabroviapp.R
import hr.foi.rampu.dabroviapp.helpers.SessionManager
import kotlinx.coroutines.DelicateCoroutinesApi

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val btnRegister: Button = findViewById(R.id.btnConfirmRegistration)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerUser()
    {
        val username = findViewById<EditText>(R.id.edtUsername).text.toString()
        val email = findViewById<EditText>(R.id.edtEmail).text.toString()
        val password = findViewById<EditText>(R.id.edtPassword).text.toString()

        if(username == "" || email == "" || password == "")
        {
            val toastMessage = getString(R.string.toastSignupEmptyError)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            return
        }

        if(!email.matches("^[\\w.\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,}(?:\\.[a-zA-Z]{2,})?\$".toRegex()))
        {
            val toastMessage = getString(R.string.toastSignupBadEmailError)
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
            return
        }

        SessionManager.login(username,password, this@RegistrationActivity) { success ->
            runOnUiThread {
                if(!success){
                    SessionManager.addUser(email, password, username, this@RegistrationActivity) { success ->
                        runOnUiThread {
                            if (success) {
                                endRegistrationActivity()
                            } else {
                                Toast.makeText(this, R.string.toastInvalidLoginError, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun endRegistrationActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}
