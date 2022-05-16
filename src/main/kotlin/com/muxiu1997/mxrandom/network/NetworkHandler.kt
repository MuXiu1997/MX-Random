package com.muxiu1997.mxrandom.network

import com.muxiu1997.mxrandom.MODNAME
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.network.FMLEventChannel
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent
import cpw.mods.fml.common.network.NetworkRegistry
import net.minecraft.client.Minecraft
import net.minecraft.network.NetHandlerPlayServer

object NetworkHandler {
    const val CHANNEL_NAME = MODNAME
    private val eventChannel: FMLEventChannel

    init {
        FMLCommonHandler.instance().bus().register(this)
        eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL_NAME)
        eventChannel.register(this)
    }

    @SubscribeEvent
    fun handleServerPacket(e: ServerCustomPacketEvent) {
        val server = e.packet.handler()
        if (server !is NetHandlerPlayServer) return
        val payload = e.packet.payload()
        val packetTypeID = payload.readInt()
        val packet = PacketTypes.getPacketType(packetTypeID).parsePacket(payload)
        packet.onServer(server.playerEntity)
    }

    @SubscribeEvent
    fun handleClientPacket(e: ClientCustomPacketEvent) {
        val payload = e.packet.payload()
        val packetTypeID = payload.readInt()
        val packet = PacketTypes.getPacketType(packetTypeID).parsePacket(payload)
        packet.onClient(Minecraft.getMinecraft().thePlayer)
    }

    fun sendToAllAround(packet: MXPacket<*>, point: NetworkRegistry.TargetPoint) {
        eventChannel.sendToAllAround(packet.getProxy(), point)
    }
}
