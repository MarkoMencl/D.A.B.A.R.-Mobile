package hr.foi.rampu.dabroviapp.ws

import hr.foi.rampu.dabroviapp.entities.Chat
import hr.foi.rampu.dabroviapp.entities.ChatPreview
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ChatService {
    @GET("chat.php")
    fun getChat(
        @Query("senderId") senderId: Int,
        @Query("receiverId") receiverId: Int
    ): Call<List<Chat>>

    @GET("chat.php")
    fun getPreviewChat(
        @Query("senderId") senderId: Int
    ): Call<List<ChatPreview>>

    @POST("chat.php")
    fun postChat(
        @Body chat: ChatRequest
    ): Call<Any>

    @PUT("chat.php")
    fun putChat(
        @Body chat: Chat
    ): Call<Chat>

    @DELETE("chat.php")
    fun deleteChat(
        @Query("senderId") senderId: Int,
        @Query("receiverId") receiverId: Int
    ): Call<Chat>
}