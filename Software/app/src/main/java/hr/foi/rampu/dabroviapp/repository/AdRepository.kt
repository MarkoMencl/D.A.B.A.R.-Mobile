package hr.foi.rampu.dabroviapp.repository

import Ad
import hr.foi.rampu.dabroviapp.ws.WsAd
import android.util.Log
import Image
import PostAd
import DeleteAdRequest
import hr.foi.rampu.dabroviapp.helpers.SessionManager


object AdRepository {

    suspend fun getAllAds(): List<Ad>? {
        return WsAd.getAllAds()
    }

    suspend fun getAdById(adId: Int): Ad? {
        val ad = WsAd.getAdById(adId)
        if (ad != null) {
            Log.d("AdRepository", "Ad retrieved: $ad")
        } else {
            Log.e("AdRepository", "Failed to fetch ad with ID: $adId")
        }
        return ad
    }


    suspend fun getUserAds(userId: Int): List<Ad>? {
        return WsAd.getUserAds(userId)
    }


    suspend fun addAd(ad: PostAd, image: Image): Boolean {
        Log.d("ADREPO", ad.toString())
        Log.d("DEBUG", "Image byte array: ${image.bitmap.toString()}")
        Log.d("ImageDebug", "Image size: ${image.bitmap.length} bytes")
        return WsAd.addAd(ad, image)
    }

    suspend fun deleteAd(adId: Int): Boolean {
        Log.d("AdRepository", "Calling delete for ad ID: $adId")
        return WsAd.deleteAd(adId)
    }

    suspend fun getAdsForTheCategory(categoryId: Int): List<Ad>? {
        return WsAd.getAdsForTheCategory(categoryId)
    }

    suspend fun updateAd(adId: Int, title: String? = null, description: String? = null, price: Double? = null): Boolean {
        Log.d("AdRepository", "Updating ad ID: $adId with title=$title, description=$description, price=$price")
        return WsAd.updateAd(adId, title, description, price)
    }


    suspend fun searchAds(keyword: String, sort: String? = null): List<Ad>? {
        val sortMapping = mapOf(
            "Price: Low to High" to "price_asc",
            "Price: High to Low" to "price_desc",
            "Date: Oldest First" to "date_asc",
            "Date: Newest First" to "date_desc",
            "Most Popular" to "popularity_desc"
        )

        val mappedSort = sortMapping[sort] ?: null
        Log.d("AdRepository", "Searching ads with keyword: '$keyword' and sort: '$mappedSort'")
        return WsAd.searchAds(keyword, mappedSort)
    }



    suspend fun getLastInsertedAdId(): Int? {
        return WsAd.getLastInsertedAdId()
    }

    suspend fun linkImageToAd(adId: Int, imageId: Int?): Boolean? {
        return imageId?.let { WsAd.linkImageToAd(adId, it) }
    }


    suspend fun addAdToFavourites(adId: Int, userId: Int): Boolean {
        return try {
            WsAd.linkFavouriteAd(adId, userId)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserFavoriteAds(): List<Ad>? {
        val currentUser = SessionManager.getUser() ?: return null
        return WsAd.getUserFavoriteAds(currentUser.id)
    }

    suspend fun deleteFavoriteAd(adId: Int, userId: Int): Boolean {
        return try {
            WsAd.deleteFavoriteAd(adId, userId)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun incrementAdViews(adId: Int): Boolean {
        return try {
            WsAd.incrementAdViews(adId)
        } catch (e: Exception) {
            false
        }
    }
}
