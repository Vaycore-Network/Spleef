package io.github.mayo8432.spleef.handler

import io.github.mayo8432.spleef.Main
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakHandler : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, Main.instance)
    }

    fun onBlockBreak(event: BlockBreakEvent) {

    }
}