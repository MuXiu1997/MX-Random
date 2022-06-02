package com.muxiu1997.mxrandom.api

import com.muxiu1997.mxrandom.MXRandom.network
import com.muxiu1997.mxrandom.network.message.MessageSyncMetaTileEntityConfig
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer

interface IConfigurableMetaTileEntity {
    fun getBaseMetaTileEntity(): IGregTechTileEntity

    fun readConfigFromBytes(buf: ByteBuf)

    fun writeConfigToBytes(buf: ByteBuf)

    fun applyConfigChanges() {
        val baseMetaTileEntity = getBaseMetaTileEntity()
        baseMetaTileEntity.markDirty()
        when {
            baseMetaTileEntity.isClientSide -> network.sendToServer(MessageSyncMetaTileEntityConfig(this))
            baseMetaTileEntity.isServerSide -> network.sendToAll(MessageSyncMetaTileEntityConfig(this))
        }
    }

    fun getServerGuiElement(ID: Int, player: EntityPlayer?): Any?

    fun getClientGuiElement(ID: Int, player: EntityPlayer?): Any?
}
