package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.tileentities.GT_TileEntity_LargeMolecularAssembler
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerTileEntity() {
        GT_TileEntity_LargeMolecularAssembler(
            MXRandom.MTE_ID_OFFSET + 1,
            "$MODID.largemolecularassembler",
            StatCollector.translateToLocal("tile.$MODID.largemolecularassembler.name")
        )
    }
}
