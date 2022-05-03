package com.muxiu1997.gtlma.proxy

import com.muxiu1997.gtlma.GT_TileEntity_LargeMolecularAssembler
import com.muxiu1997.gtlma.GtLargeMolecularAssembler.MTE_ID_OFFSET
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerTileEntity() {
        GT_TileEntity_LargeMolecularAssembler(
            MTE_ID_OFFSET + 1,
            "lma.largemolecularassembler",
            StatCollector.translateToLocal("tile.largemolecularassembler.name")
        )
    }
}
