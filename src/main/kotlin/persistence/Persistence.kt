package persistence

abstract class Persistence {
    abstract fun check(name: String): Boolean
    abstract fun save(flightPoint: FlightPoint)
    abstract fun replace(flightPoint: FlightPoint)
    abstract fun delete(name: String)
    abstract fun obtain(name: String): FlightPoint?
}