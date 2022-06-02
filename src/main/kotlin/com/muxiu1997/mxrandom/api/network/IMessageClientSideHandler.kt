package com.muxiu1997.mxrandom.api.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext

interface IMessageClientSideHandler<REQ : IMessage, REPLY : IMessage?> : IMessageHandler<REQ, REPLY> {
    override fun onMessage(message: REQ, ctx: MessageContext): REPLY {
        if (ctx.side.isServer) {
            throw IllegalAccessError("Cannot handle server-side messages")
        }
        return handleClientSideMessage(message, ctx)
    }

    fun handleClientSideMessage(message: REQ, ctx: MessageContext): REPLY
}
