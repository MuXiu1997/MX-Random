package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.blocks.BlockCraftingDisplay
import com.muxiu1997.mxrandom.blocks.TileEntityCraftingDisplay
import com.muxiu1997.mxrandom.tileentities.GT_TileEntity_LargeMolecularAssembler
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerBlocks() {
        GameRegistry.registerBlock(BlockCraftingDisplay, "craftingDisplay")
    }
    fun registerTileEntity() {
        GT_TileEntity_LargeMolecularAssembler(
            MXRandom.MTE_ID_OFFSET + 1,
            "$MODID.largemolecularassembler",
            StatCollector.translateToLocal("tile.$MODID.largemolecularassembler.name")
        )

        GameRegistry.registerTileEntity(TileEntityCraftingDisplay::class.java, "$MODID.craftingdisplay")
    }

    open fun registerRenderers() {}
}
