package hr.foi.rampu.dabroviapp.repository

import Ad
import Image
import android.util.Log
import hr.foi.rampu.dabroviapp.ws.WsAd
import hr.foi.rampu.dabroviapp.ws.WsImg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImgRepository {

    suspend fun getLastInsertedImgId(): Int? {
        return WsImg.getLastInsertedImgId()
    }

    suspend fun getTheImageByAdId(adId : Int): Image? {
        return WsImg.getTheImageByAdId(adId)
    }

    suspend fun getImagesByAdId(adId: Int): List<Image>? {
        return WsImg.getImagesByAdId(adId)
    }
}
