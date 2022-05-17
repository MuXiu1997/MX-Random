package com.muxiu1997.mxrandom.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import cpw.mods.fml.relauncher.Side

interface IMessageBothSideHandler<REQ : IMessage, REPLY : IMessage?> :
    IMessageServerSideHandler<REQ, REPLY>, IMessageClientSideHandler<REQ, REPLY> {
    override fun onMessage(message: REQ, ctx: MessageContext): REPLY {
        return if (ctx.side == Side.SERVER) {
            handleServerSideMessage(message, ctx)
        } else {
            handleClientSideMessage(message, ctx)
        }
    }
}
