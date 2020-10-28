import org.bukkit.plugin.java.JavaPlugin
import persistence.DebugPersistence
import persistence.Persistence
import java.util.logging.Logger

class McersIFR : JavaPlugin() {

    companion object {
        lateinit var instance: McersIFR
    }
    var log: Logger = logger
    lateinit var persistence: Persistence

    override fun onEnable() {
        log.info("Enabling McersIFR...")
        instance = this
        persistence = DebugPersistence() // TODO: Setup a persistence method

        // Register our command "ifr" (set an instance of your command class as executor)
        getCommand("ifr")!!.setExecutor(CommandIfr())
    }

    override fun onDisable() {
        logger.info("Disabling McersIFR...")
    }
}