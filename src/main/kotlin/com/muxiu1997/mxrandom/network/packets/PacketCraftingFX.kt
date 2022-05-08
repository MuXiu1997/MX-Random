package com.muxiu1997.mxrandom.network.packets

import appeng.api.storage.data.IAEItemStack
import appeng.util.item.AEItemStack
import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.client.fx.CraftingFX
import com.muxiu1997.mxrandom.network.MXPacket
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer

class PacketCraftingFX : MXPacket<PacketCraftingFX.Companion.Data> {
    @Suppress("unused")
    constructor(buf: ByteBuf) : super(buf)
    constructor(data: Data) : super(data)
    constructor(x: Int, y: Int, z: Int, age: Int, itemStack: IAEItemStack) : this(Data(x, y, z, age, itemStack))

    override fun readFromStream(stream: ByteBuf): Data {
        return Data(
            stream.readInt(),
            stream.readInt(),
            stream.readInt(),
            stream.readInt(),
            AEItemStack.loadItemStackFromPacket(stream)
        )
    }


    override fun writeToStream(data: Data, buf: ByteBuf) {
        buf.writeInt(data.x)
        buf.writeInt(data.y)
        buf.writeInt(data.z)
        buf.writeInt(data.age)
        data.itemStack.writeToPacket(buf)
    }


    @SideOnly(Side.CLIENT)
    override fun onClient(data: Data, player: EntityPlayer) {
        val fx = CraftingFX(
            Minecraft.getMinecraft().theWorld,
            data.x.toDouble(),
            data.y.toDouble(),
            data.z.toDouble(),
            data.age,
            data.itemStack
        )
        Minecraft.getMinecraft().effectRenderer.addEffect(fx)
    }

    companion object {
        data class Data(val x: Int, val y: Int, val z: Int, val age: Int, val itemStack: IAEItemStack)
    }
}
