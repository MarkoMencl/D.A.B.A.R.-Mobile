package hr.foi.rampu.dabroviapp.ws

import hr.foi.rampu.dabroviapp.entities.Review
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ReviewService {
    @GET("review.php")
    fun getSenderReview(
        @Query("senderId") senderId: Int,
    ): Call<List<Review>>

    @GET("review.php")
    fun getReceiverReview(
        @Query("receiverId") receiverId: Int
    ): Call<List<Review>>

    @POST("review.php")
    fun postReview(
        @Body chat: ReviewRequest
    ): Call<Any>

    @PUT("review.php")
    fun putReview(
        @Body chat: ReviewRequestUpdate
    ): Call<Any>

    @DELETE("review.php")
    fun deleteReview(
        @Query("reviewerId") reviewerId: Int,
        @Query("revieweeId") revieweeId: Int
    ): Call<Any>
}