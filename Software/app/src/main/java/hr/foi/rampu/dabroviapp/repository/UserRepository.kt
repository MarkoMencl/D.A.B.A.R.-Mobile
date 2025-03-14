package hr.foi.rampu.dabroviapp.repository

import Ad
import Image
import android.util.Log
import hr.foi.rampu.dabroviapp.entities.User
import hr.foi.rampu.dabroviapp.ws.WsAd
import hr.foi.rampu.dabroviapp.ws.WsImg
import hr.foi.rampu.dabroviapp.ws.WsUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserRepository {

    suspend fun getUserById(userId: Int): User? {
        val user = WsUser.getUserById(userId)
        if (user != null) {
            Log.d("AdRepository", "Ad retrieved: $user")
        } else {
            Log.e("AdRepository", "Failed to fetch ad with ID: $userId")
        }
        return user
    }
}
