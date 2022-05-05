package com.muxiu1997.gtlma.proxy

import com.muxiu1997.gtlma.tileentities.GT_TileEntity_LargeMolecularAssembler
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerTileEntity() {
        GT_TileEntity_LargeMolecularAssembler(
            GT_TileEntity_LargeMolecularAssembler.MTE_ID,
            "lma.largemolecularassembler",
            StatCollector.translateToLocal("tile.lma.largemolecularassembler.name")
        )
    }
}
