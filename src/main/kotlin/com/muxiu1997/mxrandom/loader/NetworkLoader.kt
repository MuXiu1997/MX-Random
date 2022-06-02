package com.muxiu1997.mxrandom.loader

import com.muxiu1997.mxrandom.MODID
import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.api.network.IMessageClientSideHandler
import com.muxiu1997.mxrandom.api.network.IMessageServerSideHandler
import com.muxiu1997.mxrandom.network.GuiHandler
import com.muxiu1997.mxrandom.network.message.MessageCraftingFX
import com.muxiu1997.mxrandom.network.message.MessageSyncMetaTileEntityConfig
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.relauncher.Side

object NetworkLoader {
    fun load(@Suppress("UNUSED_PARAMETER") e: FMLInitializationEvent) {
        register(MessageCraftingFX.Companion.Handler)
        register(MessageSyncMetaTileEntityConfig.Companion.Handler)

        NetworkRegistry.INSTANCE.registerGuiHandler(MODID, GuiHandler)
    }

    private var networkMessageID = 0

    private inline fun <reified M : IMessage> register(handler: IMessageHandler<M, *>) {
        if (handler is IMessageServerSideHandler) {
            MXRandom.network.registerMessage(handler, M::class.java, networkMessageID++, Side.SERVER)
        }
        if (handler is IMessageClientSideHandler) {
            MXRandom.network.registerMessage(handler, M::class.java, networkMessageID++, Side.CLIENT)
        }
    }
}
