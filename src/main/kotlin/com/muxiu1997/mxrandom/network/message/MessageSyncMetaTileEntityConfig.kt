package com.muxiu1997.mxrandom.network.message

import com.muxiu1997.mxrandom.MODID
import com.muxiu1997.mxrandom.MXRandom.network
import com.muxiu1997.mxrandom.api.IConfigurableMetaTileEntity
import com.muxiu1997.mxrandom.api.network.IMessageBothSideHandler
import com.muxiu1997.mxrandom.getMetaTileEntity
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraftforge.common.DimensionManager
import kotlin.properties.Delegates

class MessageSyncMetaTileEntityConfig : IMessage {
    var x by Delegates.notNull<Int>()
    var y by Delegates.notNull<Int>()
    var z by Delegates.notNull<Int>()
    var dimID by Delegates.notNull<Int>()
    lateinit var configData: ByteBuf
    var openGui by Delegates.notNull<Boolean>()

    @Suppress("unused")
    constructor()

    constructor(metaTileEntity: IConfigurableMetaTileEntity, openGui: Boolean = false) {
        val baseMetaTileEntity = metaTileEntity.getBaseMetaTileEntity()
        this.x = baseMetaTileEntity.xCoord
        this.y = baseMetaTileEntity.yCoord.toInt()
        this.z = baseMetaTileEntity.zCoord
        this.dimID = baseMetaTileEntity.world.provider.dimensionId
        this.configData = Unpooled.buffer()
        metaTileEntity.writeConfigToBytes(this.configData)
        this.openGui = openGui
    }

    @Suppress("unused")
    constructor(x: Int, y: Int, z: Int, dimID: Int, configData: ByteBuf, openGui: Boolean = false) {
        this.x = x
        this.y = y
        this.z = z
        this.dimID = dimID
        this.configData = configData
        this.openGui = openGui
    }

    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        y = buf.readInt()
        z = buf.readInt()
        dimID = buf.readInt()
        configData = buf.readBytes(buf.readInt())
        openGui = buf.readBoolean()
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
        buf.writeInt(dimID)
        buf.writeInt(configData.readableBytes())
        buf.writeBytes(configData)
        buf.writeBoolean(openGui)
    }

    companion object {
        object Handler : IMessageBothSideHandler<MessageSyncMetaTileEntityConfig, IMessage?> {
            override fun handleClientSideMessage(
                message: MessageSyncMetaTileEntityConfig,
                ctx: MessageContext
            ): IMessage? {
                val world = DimensionManager.getWorld(message.dimID) ?: return null
                val metaTileEntity = world.getMetaTileEntity(message.x, message.y, message.z)
                    as? IConfigurableMetaTileEntity ?: return null
                metaTileEntity.readConfigFromBytes(message.configData)
                if (message.openGui) {
                    Minecraft.getMinecraft().thePlayer.openGui(MODID, 1, world, message.x, message.y, message.z)
                }
                return null
            }

            override fun handleServerSideMessage(
                message: MessageSyncMetaTileEntityConfig,
                ctx: MessageContext
            ): IMessage? {
                val world = DimensionManager.getWorld(message.dimID) ?: return null
                val metaTileEntity = world.getMetaTileEntity(message.x, message.y, message.z)
                    as? IConfigurableMetaTileEntity ?: return null
                val configData = message.configData.copy()
                metaTileEntity.readConfigFromBytes(message.configData)

                message.openGui = false
                message.configData = configData
                network.sendToAll(message)
                return null
            }
        }
    }
}
