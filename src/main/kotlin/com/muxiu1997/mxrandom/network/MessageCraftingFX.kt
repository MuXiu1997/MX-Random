package com.muxiu1997.mxrandom.network

import appeng.api.storage.data.IAEItemStack
import appeng.util.item.AEItemStack
import com.muxiu1997.mxrandom.client.fx.CraftingFX
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import kotlin.properties.Delegates

class MessageCraftingFX : IMessage {
    var x by Delegates.notNull<Int>()
    var y by Delegates.notNull<Int>()
    var z by Delegates.notNull<Int>()
    var age by Delegates.notNull<Int>()
    lateinit var itemStack: IAEItemStack

    @Suppress("unused")
    constructor()

    constructor(x: Int, y: Int, z: Int, age: Int, itemStack: IAEItemStack) {
        this.x = x
        this.y = y
        this.z = z
        this.age = age
        this.itemStack = itemStack
    }

    override fun fromBytes(buf: ByteBuf) {
        x = buf.readInt()
        y = buf.readInt()
        z = buf.readInt()
        age = buf.readInt()
        itemStack = AEItemStack.loadItemStackFromPacket(buf)
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(x)
        buf.writeInt(y)
        buf.writeInt(z)
        buf.writeInt(age)
        itemStack.writeToPacket(buf)
    }

    companion object {
        object Handler : IMessageClientSideHandler<MessageCraftingFX, IMessage?> {
            override fun handleClientSideMessage(message: MessageCraftingFX, ctx: MessageContext): IMessage? {
                val fx = CraftingFX(
                    Minecraft.getMinecraft().theWorld,
                    message.x.toDouble(),
                    message.y.toDouble(),
                    message.z.toDouble(),
                    message.age,
                    message.itemStack
                )
                Minecraft.getMinecraft().effectRenderer.addEffect(fx)
                return null
            }
        }
    }
}
