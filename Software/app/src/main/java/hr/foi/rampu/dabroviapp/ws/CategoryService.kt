package hr.foi.rampu.dabroviapp.ws

import Ad
import Location
import hr.foi.rampu.dabroviapp.entities.Category
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CategoryService {
    @GET("category.php")
    suspend fun getAllCategories(): Response<List<Category>>
}
