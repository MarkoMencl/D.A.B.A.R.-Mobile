package hr.foi.rampu.dabroviapp.ws

import hr.foi.rampu.dabroviapp.entities.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface UserService {
    @GET("user.php")
    fun getUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<User>

    @POST("user.php")
    fun postUser(
        @Body user: UserRequest
    ): Call<User>

    @PUT("user.php")
    fun putUser(
        @Body updateUserRequest: UpdateUserRequest
    ): Call<User>


    @DELETE("user.php")
    fun deleteUser(
        @Query("id") id: Int
    ): Call<User>

    @GET("ad-fetch.php")
    suspend fun getUserById(@Query("userId") userId: Int): Response<User>
}
