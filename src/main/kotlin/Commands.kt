import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.inventory.meta.ItemMeta
import persistence.FlightPoint
import java.lang.NullPointerException

// TODO: Permissions not checked
class CommandIfr : CommandExecutor {
    // This method is called, when somebody uses ifr command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {

            // /ifr [option] [something]
            when (args[0]) {
                "set" -> ifrSet(sender, args.slice(1 until args.size))
                "mod" -> ifrModify(sender, args.slice(1 until args.size))
                "del" -> ifrDelete(sender, args.slice(1 until args.size))
                "debug" -> {
                    if (sender is Player) {
                        McersIFR.instance.log.info("DEBUG: itemStack ${sender.inventory.itemInMainHand}")
                        McersIFR.instance.log.info("DEBUG: itemMeta ${sender.inventory.itemInMainHand.itemMeta}")
                    }
                }
                else -> ifrSelect(sender, args.toList())
            }

        // /ifr
        } else {
            ifrInfo(sender)
        }
        return true
    }

    // /ifr set [something?]
    // Set a new fly point
    private fun ifrSet(sender: CommandSender, args: List<String>) {
        McersIFR.instance.log.info("DEBUG: ifrSet args $args")
        if (args.isEmpty()) { // No args
            sender.sendMessage("This command (/ifr set) set a new flight point in a lodestone. You can use the block " +
                    "you are looking or coordinates at beginning. Usage: /ifr set [x] [y] [z] [world] <name>")
        } else {
            val flightPoint = FlightPoint

            if (args.size >= 5
                    && args[0].toDoubleOrNull() != null
                    && args[1].toDoubleOrNull() != null
                    && args[2].toDoubleOrNull() != null) {
                // Start with coordinates
                val name: String = args.slice(3 until args.size - 1).joinToString(separator = " ")
                if (!McersIFR.instance.persistence.check(name)) {
                    flightPoint.location = Location(
                            Bukkit.getServer().getWorld(args[3]),
                            args[0].toDouble(),
                            args[1].toDouble(),
                            args[2].toDouble()
                    )
                    flightPoint.name = name
                    if (Bukkit.getServer().getWorld(args[3])?.getBlockAt(flightPoint.location)?.type != Material.LODESTONE) {
                        sender.sendMessage("No lodestone block placed at coordinates.")
                        return
                    }
                } else {
                    sender.sendMessage("Already exist a flight point named: \"$name\".")
                    return
                }

            } else {
                // Not start with coordinates
                if (sender is Player) {
                    val targetBlock: Block
                    try {
                        targetBlock = sender.getTargetBlockExact(10)!!
                    } catch (ex: NullPointerException) {
                        sender.sendMessage("Error when retrieving the block you are looking.")
                        return
                    }

                    flightPoint.location = targetBlock.location
                    flightPoint.name = args.joinToString(separator = " ")

                    if (sender.world.getBlockAt(flightPoint.location).type != Material.LODESTONE) {
                        sender.sendMessage("You must look a Lodestone block.")
                        return
                    }
                } else {
                    sender.sendMessage("Only players can use this command without specify coordinates.")
                    return
                }
            }
            McersIFR.instance.persistence.save(flightPoint)
            sender.sendMessage("New flight point set: \"${flightPoint.name}\".")
        }
    }

    // /ifr mod [something?]
    // Modify an existing flight point
    private fun ifrModify(sender: CommandSender, args: List<String>) {
        McersIFR.instance.log.info("DEBUG: ifrModify args $args")
        if (args.isEmpty()) { // No args
            sender.sendMessage("This command (/ifr mod) modify a existing flight point. You can use the block " +
                    "you are looking or coordinates at beginning. Usage: /ifr mod [x] [y] [z] [world] <name>")
        } else {
            val flightPoint = FlightPoint

            if (args.size >= 5
                    && args[0].toDoubleOrNull() != null
                    && args[1].toDoubleOrNull() != null
                    && args[2].toDoubleOrNull() != null) {
                // Start with coordinates
                val name: String = args.slice(3 until args.size - 1).joinToString(separator = " ")
                if (McersIFR.instance.persistence.check(name)) {
                    flightPoint.location = Location(
                            Bukkit.getServer().getWorld(args[3]),
                            args[0].toDouble(),
                            args[1].toDouble(),
                            args[2].toDouble()
                    )
                    flightPoint.name = name

                    if (Bukkit.getServer().getWorld(args[3])?.getBlockAt(flightPoint.location)?.type != Material.LODESTONE) {
                        sender.sendMessage("No lodestone block placed at coordinates.")
                        return
                    }
                } else {
                    sender.sendMessage("No existing flight point named: \"$name\".")
                    return
                }

            } else {
                // Not start with coordinates
                if (sender is Player) {
                    val targetBlock: Block
                    try {
                        targetBlock = sender.getTargetBlockExact(10)!!
                    } catch (ex: NullPointerException) {
                        sender.sendMessage("Error when retrieving the block you are looking.")
                        return
                    }

                    val name: String = args.joinToString(separator = " ")
                    if (McersIFR.instance.persistence.check(name)) {
                        flightPoint.location = targetBlock.location
                        flightPoint.name = name
                    } else {
                        sender.sendMessage("No existing flight point named: \"$name\".")
                        return
                    }

                    if (sender.world.getBlockAt(flightPoint.location).type != Material.LODESTONE) {
                        sender.sendMessage("You must look a Lodestone block.")
                        return
                    }
                } else {
                    sender.sendMessage("Only players can use this command without specify coordinates.")
                    return
                }
            }
            McersIFR.instance.persistence.save(flightPoint)
            sender.sendMessage("Modified flight point: \"${flightPoint.name}\".")
        }
    }

    // /ifr del [something?]
    // Delete a existent fly point
    private fun ifrDelete(sender: CommandSender, args: List<String>) {
        McersIFR.instance.log.info("DEBUG: ifrDelete args $args")
        if (args.isEmpty()) { // No args
            sender.sendMessage("This command (/ifr del) delete a existing flight point. Usage: /ifr del [name]")
        } else {
            val name: String = args.joinToString(separator = " ")
            if (McersIFR.instance.persistence.check(name)) {
                McersIFR.instance.persistence.delete(name)
                sender.sendMessage("Deleted flight point: \"$name\".")
            } else {
                sender.sendMessage("No existing flight point named: \"$name\".")
                return
            }
        }
    }

    // /ifr [something?]
    // Select a fly point for your compass
    private fun ifrSelect(sender: CommandSender, args: List<String>) {
        McersIFR.instance.log.info("DEBUG: ifrSelect args $args")
        val name: String = args.joinToString(separator = " ")
        if (sender is Player
                && McersIFR.instance.persistence.check(name)) {
            val invSlot = sender.inventory.heldItemSlot

            val itemInHandStack = sender.inventory.getItem(invSlot)
            val itemInHandMeta = itemInHandStack?.itemMeta

            if (itemInHandMeta is CompassMeta) {
                sender.inventory.clear(invSlot)
                itemInHandMeta.isLodestoneTracked = true
                val location = McersIFR.instance.persistence.obtain(name)?.location
                itemInHandMeta.lodestone = location
                itemInHandMeta.setDisplayName(name)

                itemInHandStack.itemMeta = itemInHandMeta
                sender.inventory.setItemInMainHand(itemInHandStack)
                sender.sendMessage("Compass now pointing to: \"$name\".")
            } else {
                sender.sendMessage("You need a compass in your main hand.")
            }
        } else {
            sender.sendMessage("No existing flight point named: \"$name\".")
            return
        }
    }

    // /ifr
    // Info
    private fun ifrInfo(sender: CommandSender) {
        sender.sendMessage("How to use McersIFR: Put a compass in your hand and use /ifr [destination]. The compass" +
                " will point to your desired destination.")
    }
}