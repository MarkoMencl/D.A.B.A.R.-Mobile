package hr.foi.rampu.dabroviapp.repository

import Ad
import Location
import hr.foi.rampu.dabroviapp.ws.WsAd
import android.util.Log
import hr.foi.rampu.dabroviapp.entities.Category
import hr.foi.rampu.dabroviapp.ws.WsCategory
import hr.foi.rampu.dabroviapp.ws.WsLocation


object CategoryRepository {

    suspend fun getAllCategories(): List<Category>? {
        return WsCategory.getAllCategories()
    }
}
