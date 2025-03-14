package hr.foi.rampu.dabroviapp.helpers

import Location
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import hr.foi.rampu.dabroviapp.entities.Category
import hr.foi.rampu.dabroviapp.entities.User
import hr.foi.rampu.dabroviapp.ws.UpdateUserRequest
import hr.foi.rampu.dabroviapp.ws.UserRequest
import hr.foi.rampu.dabroviapp.ws.WsUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object SessionManager {
    private lateinit var sharedPreferences: SharedPreferences
    private var locationsList: List<Location>? = null
    private var categoryList: List<Category>? = null


    fun saveLocations(locations: List<Location>) {
        locationsList = locations
    }
    fun saveCategories(categories: List<Category>) {
        categoryList = categories
    }

    fun getLocations(): List<Location>? {
        return locationsList
    }
    fun getCategories(): List<Category>? {
        return categoryList
    }


    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    }

    private fun setUser(user: User) {
        val userJson = Gson().toJson(user)
        sharedPreferences.edit()
            .putString("USER", userJson)
            .apply()
    }

    fun getUser(): User? {
        if (!::sharedPreferences.isInitialized) {
            return null
        }
        val userJson = sharedPreferences.getString("USER", null)
        return if (userJson != null) {
            Gson().fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("IS_LOGGED_IN", isLoggedIn).apply()
        Log.d("SessionManager", "Logged In state set to: $isLoggedIn")
    }

    fun setUserEmail(email: String) {
        sharedPreferences.edit().putString("USER_EMAIL", email).apply()
        Log.d("SessionManager", "User Email set: $email")
    }

    fun isLoggedIn(): Boolean {
        if (!::sharedPreferences.isInitialized) {
            return false
        }
        val loggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        Log.d("SessionManager", "isLoggedIn() called, returns: $loggedIn")
        return loggedIn
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("USER_EMAIL", null)
    }

    fun addUser(email: String, password: String, username: String, context: Context, callback: (Boolean) -> Unit) {
        val hashedPassword = hashPasswordWithSalt(password, "moja sol")
        val newUser = UserRequest(username,hashedPassword,email)

        WsUser.userService.postUser(newUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful){
                    login(username, password, context, callback)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun login(username: String, password: String, context: Context, callback: (Boolean) -> Unit) {
        val hashedPassword = hashPasswordWithSalt(password, "moja sol")

        WsUser.userService.getUser(username,hashedPassword).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.isSuccessful && response.body() != null){
                    val user = response.body()

                    if(user != null && user.id != 0){
                        setUser(user)
                        setLoggedIn(true)
                        callback(true)
                    }else{
                        setLoggedIn(false)
                        callback(false)
                    }
                }else{
                    setLoggedIn(false)
                    callback(false)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show()
                callback(false)
            }
        })
    }

    private fun hashPasswordWithSalt(password: String, salt: String): String {
        val saltedPassword = password + salt
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(saltedPassword.toByteArray(StandardCharsets.UTF_8))
        return hashedBytes.joinToString("") { String.format("%02x", it) }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
        setLoggedIn(false)
        Log.d("SessionManager", "User logged out")
    }

    suspend fun updateUserProfile(
        email: String,
        username: String,
        contact: String,
        imageName: String,
        language: String,
        selectedLocation: Int?
    ) {
        val currentUser = getUser()
        if (currentUser == null) {
            Log.e("SessionManager", "No logged-in user to update.")
            return
        }
        Log.d("SessionManager", "User profile : $email, $username, $contact, $imageName, $language, $selectedLocation")

        withContext(Dispatchers.IO) {
            val updateUserRequest = UpdateUserRequest(
                username = username,
                email = email,
                contact = contact,
                profileImg = imageName,
                language = language,
                locationId = selectedLocation
            )
            Log.d("SessionManager", "Sending request with data: $updateUserRequest")
            val call = WsUser.userService.putUser(updateUserRequest)

            try {
                val response = call.execute()
                Log.d("SessionManager", "Response code: ${response.code()}")
                Log.d("SessionManager", "Response body: ${response.body()?.toString()}")
                Log.d("SessionManager", "Raw response: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {

                    val updatedUser = User(
                        id = currentUser.id,
                        username = username,
                        email = email,
                        contact = contact,
                        profileImg = imageName,
                        language = language,
                        locationId = selectedLocation
                    )

                    setUser(updatedUser)

                    Log.d("SessionManager", "User profile updated successfully (from request data): $updatedUser")
                } else {

                }
            } catch (e: Exception) {
                Log.e("SessionManager", "Error while updating user profile", e)
            }
        }
    }

    fun setUsername(username: String) {
        sharedPreferences.edit().putString("USERNAME_KEY", username).apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("USERNAME_KEY", null)
    }
}