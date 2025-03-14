package hr.foi.rampu.dabroviapp.repository

import Location
import hr.foi.rampu.dabroviapp.ws.WsLocation


object LocationRepository {

    suspend fun getAllLocations(): List<Location>? {
        return WsLocation.getAllLocations()
    }

    suspend fun getLocationById(id: Int): Location? {
        return WsLocation.getLocationById(id)
    }
}
