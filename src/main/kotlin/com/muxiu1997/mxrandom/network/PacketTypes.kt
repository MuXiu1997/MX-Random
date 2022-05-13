package com.muxiu1997.mxrandom.network

import com.muxiu1997.mxrandom.network.packets.PacketCraftingFX
import io.netty.buffer.ByteBuf

enum class PacketTypes(private val packetClass: Class<out MXPacket<*>>) {
    CRAFTING_FX(PacketCraftingFX::class.java),
    ;


    fun parsePacket(buf: ByteBuf): MXPacket<*> {
        return packetClass.getConstructor(ByteBuf::class.java).newInstance(buf)
    }

    companion object {
        private lateinit var lookup: HashMap<Class<out MXPacket<*>>, PacketTypes>
        fun getPacketType(id: Int): PacketTypes {
            return values()[id]
        }

        fun getPacketType(packetClass: Class<out MXPacket<*>>): PacketTypes {
            if (!::lookup.isInitialized) {
                lookup = HashMap()
                for (type in values()) {
                    lookup[type.packetClass] = type
                }
            }
            return lookup[packetClass] ?: throw IllegalStateException("PacketType not found for class $packetClass")
        }
    }
}

