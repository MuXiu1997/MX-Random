package com.muxiu1997.mxrandom.network

import com.muxiu1997.mxrandom.network.NetworkHandler.CHANNEL_NAME
import cpw.mods.fml.common.network.internal.FMLProxyPacket
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.entity.player.EntityPlayer

abstract class MXPacket<T> {
    private var data: T

    constructor(stream: ByteBuf) {
        this.data = this.readFromStream(stream)
    }

    constructor(data: T) {
        this.data = data
        val buf = Unpooled.buffer()
        buf.writeInt(PacketTypes.getPacketType(this.javaClass).ordinal)
        this.writeToStream(this.data, buf)
        buf.capacity(buf.readableBytes())
        this.packetStream = buf
    }

    private lateinit var packetStream: ByteBuf

    abstract fun readFromStream(stream: ByteBuf): T
    abstract fun writeToStream(data: T, buf: ByteBuf)

    fun getProxy(): FMLProxyPacket {
        if (!this::packetStream.isInitialized)
            throw IllegalStateException("The Packet constructed from data is the only way to get the FMLProxyPacket")
        return FMLProxyPacket(packetStream, CHANNEL_NAME)
    }

    @SideOnly(Side.SERVER)
    fun onServer(player: EntityPlayer) {
        this.onServer(this.data, player)
    }

    open fun onServer(data: T, player: EntityPlayer) {
        TODO("packet ${javaClass.simpleName} does not have onServer implementation")
    }

    @SideOnly(Side.CLIENT)
    fun onClient(player: EntityPlayer) {
        this.onClient(this.data, player)
    }

    open fun onClient(data: T, player: EntityPlayer) {
        TODO("packet ${javaClass.simpleName} does not have onClient implementation")
    }
}
