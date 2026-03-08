package io.github.mayo8432.spleef.handler

import de.c4vxl.gamemanager.gma.player.GMAPlayer.Companion.gma
import io.github.mayo8432.spleef.Main
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent

class ProjectileHandler : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    @EventHandler
    fun onBlockHit (event: ProjectileHitEvent) {

        // @param The Block that was hit by the projectile: can be null
        val block = event.hitBlock ?: return

        // Returns if the player is not in a game
        if (!(event.entity.shooter as Player).gma.isInGame) return

        // Returns if the block type is not a snow block
        if (block.type != Material.SNOW_BLOCK) return

        // Swaps the snow block for air
        block.type = Material.AIR
    }

    @EventHandler
    fun onPlayerHit (event: ProjectileHitEvent) {

        // @param Only react, if the victim is a player
        val player = event.hitEntity as? Player ?: return
        // @param Only reacts, if the thrown item is a snowball
        val snowball = event.entity as? Snowball ?: return

        // Returns if the player is not in a game
        if (!player.gma.isInGame) return

        // Cancels event
        event.isCancelled = true
    }
}