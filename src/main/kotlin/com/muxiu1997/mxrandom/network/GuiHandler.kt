package com.muxiu1997.mxrandom.network

import com.muxiu1997.mxrandom.api.IConfigurableMetaTileEntity
import com.muxiu1997.mxrandom.getMetaTileEntity
import cpw.mods.fml.common.network.IGuiHandler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World


object GuiHandler : IGuiHandler {
    const val ID_CONFIG_META_TILE_ENTITY = 1

    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        when (ID) {
            ID_CONFIG_META_TILE_ENTITY -> {
                val metaTileEntity = world?.getMetaTileEntity(x, y, z) as? IConfigurableMetaTileEntity ?: return null
                return metaTileEntity.getServerGuiElement(ID, player)
            }
        }
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        when (ID) {
            ID_CONFIG_META_TILE_ENTITY -> {
                val metaTileEntity = world?.getMetaTileEntity(x, y, z) as? IConfigurableMetaTileEntity ?: return null
                return metaTileEntity.getClientGuiElement(ID, player)
            }
        }
        return null
    }
}
