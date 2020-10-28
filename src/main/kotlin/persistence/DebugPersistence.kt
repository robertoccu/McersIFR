package persistence

import org.bukkit.Bukkit
import org.bukkit.Location

// TODO: Do check here or not? Throw Exceptions/Errors?
class DebugPersistence: Persistence() {
    private var memory = HashMap<String, FlightPoint>()

    override fun check(name: String): Boolean {
        McersIFR.instance.log.info("DEBUG: Check $name")
        return memory.containsKey(name)
    }

    override fun save(flightPoint: FlightPoint) {
        if (!check(flightPoint.name)) {
            McersIFR.instance.log.info("DEBUG: Save ${flightPoint.name}")
            memory[flightPoint.name] = flightPoint
        }
    }

    override fun replace(flightPoint: FlightPoint) {
        if (check(flightPoint.name)){
            delete(flightPoint.name)
            save(flightPoint)
        }
    }

    override fun delete(name: String) {
        if (check(name)){
            McersIFR.instance.log.info("DEBUG: Delete $name")
            memory.remove(name)
        }
    }

    override fun obtain(name: String): FlightPoint? {
        McersIFR.instance.log.info("DEBUG: Try obtain $name")
        return if (check(name)) {
            memory[name]
        } else {
            null
        }
    }
}