package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.blocks.TileEntityCraftingDisplay
import com.muxiu1997.mxrandom.client.renderers.TileEntityCraftingDisplayRenderer
import cpw.mods.fml.client.registry.ClientRegistry


class ClientProxy : CommonProxy() {
    override fun registerRenderers() {
        super.registerRenderers()
        ClientRegistry.bindTileEntitySpecialRenderer(
            TileEntityCraftingDisplay::class.java,
            TileEntityCraftingDisplayRenderer
        )
    }
}
