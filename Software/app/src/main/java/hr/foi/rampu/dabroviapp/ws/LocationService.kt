package hr.foi.rampu.dabroviapp.ws

import Location
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {
    @GET("location.php")
    suspend fun getAllLocations(): Response<List<Location>>

    @GET("location.php")
    suspend fun getLocationById(@Query("id") id: Int): Response<Location>
}
