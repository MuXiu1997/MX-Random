package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.blocks.TileEntityCraftingDisplay
import com.muxiu1997.mxrandom.client.renderers.TileEntityCraftingDisplayRenderer
import cpw.mods.fml.client.registry.ClientRegistry
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity


class ClientProxy : CommonProxy() {
    override fun registerRenderers() {
        super.registerRenderers()
        TileEntityCraftingDisplayRenderer bindTo TileEntityCraftingDisplay::class.java When always
    }

    companion object {
        // region :P
        private infix fun TileEntitySpecialRenderer.bindTo(other: Class<out TileEntity>): () -> Unit {
            return fun() {
                ClientRegistry.bindTileEntitySpecialRenderer(other, this)
            }
        }
        // endregion
    }
}
