package io.github.mayo8432.spleef.handler

import de.c4vxl.gamelobby.events.lobby.LobbyPlayerEquipEvent
import de.c4vxl.gamelobby.events.queue.LobbyPlayerQueueJoinedEvent
import de.c4vxl.gamelobby.utils.ScrollableInventory
import de.c4vxl.gamemanager.gma.GMA
import de.c4vxl.gamemanager.gma.game.type.GameSize
import de.c4vxl.gamemanager.gma.player.GMAPlayer.Companion.gma
import de.c4vxl.gamemanager.language.Language.Companion.language
import de.c4vxl.gamemanager.utils.ItemBuilder
import io.github.mayo8432.spleef.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class LobbyHandler : Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)

        var i = 0
        Bukkit.getScheduler().runTaskTimer(Main.instance, Runnable {
            GMA.registeredGames.forEach { game ->
                if (!game.isQueuing)
                    return@forEach

                game.players.forEach {
                    val language = it.language.child("spleef")

                    it.bukkitPlayer.sendActionBar(language.getCmp("queue.waiting",
                        ".".repeat(i + 1), (game.players.size - 1).toString(), (game.size.maxPlayers - 1).toString()
                    ))
                }
            }

            i = if (i < 2) i+1 else 0
        }, 0, 20)
    }

    private val sizeItems = ConcurrentHashMap<String, List<ItemStack>>()

    @EventHandler
    fun onLobbyEquip(event: LobbyPlayerEquipEvent) {
        val visibilityItem = event.player.inventory.getItem(4)
        val privateGameItem = event.player.inventory.getItem(5)

        event.player.inventory.clear()

        event.player.inventory.setItem(1, visibilityItem)
        event.player.inventory.setItem(2, privateGameItem)

        event.player.inventory.setItem(7, ItemBuilder(Material.PAPER, event.player.language.child("spleef").getCmp("lobby.item.join"))
            .onEvent(PlayerInteractEvent::class.java) {
                if (!it.action.isRightClick)
                    return@onEvent

                ScrollableInventory(
                    sizeItems.computeIfAbsent(it.player.language.name) {
                        GMA.possibleGameSizes.map { size ->
                            ItemBuilder(Material.PAPER, Component.text(size).color(NamedTextColor.GRAY), lore = listOf(
                                event.player.language.child("spleef").getCmp("lobby.ui.join.item.desc")
                            ))
                                .onEvent(InventoryClickEvent::class.java) { e ->
                                    (e.whoClicked as? Player)?.gma?.join(GMA.getOrCreate(
                                        GameSize.fromString(size)!!
                                    ))
                                    e.whoClicked.closeInventory()
                                }
                                .build()
                        }
                    }.toMutableList(),
                    event.player.language.child("spleef").getCmp("lobby.ui.join.title"),
                    event.player
                ).open()
            }
            .build())
    }

    @EventHandler
    fun onEquip(event: LobbyPlayerQueueJoinedEvent) {
        // Remove map vote item
        event.player.inventory.setItem(4, null)
    }
}