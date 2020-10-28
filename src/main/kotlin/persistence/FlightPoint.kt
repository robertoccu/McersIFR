package persistence

import org.bukkit.Location

object FlightPoint {

    enum class FlyPointType {
        SPAWN, PORTAL, CONSTRUCTION
    }

    lateinit var location: Location
    lateinit var name: String
    lateinit var type: FlyPointType
}