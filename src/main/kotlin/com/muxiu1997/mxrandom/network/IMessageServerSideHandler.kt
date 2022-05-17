package com.muxiu1997.mxrandom.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext

interface IMessageServerSideHandler<REQ : IMessage, REPLY : IMessage?> : IMessageHandler<REQ, REPLY> {
    override fun onMessage(message: REQ, ctx: MessageContext): REPLY {
        if (ctx.side.isClient) {
            throw IllegalAccessError("Cannot handle client-side messages")
        }
        return handleServerSideMessage(message, ctx)
    }

    fun handleServerSideMessage(message: REQ, ctx: MessageContext): REPLY
}
