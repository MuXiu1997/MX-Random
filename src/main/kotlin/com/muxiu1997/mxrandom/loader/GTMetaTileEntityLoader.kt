package com.muxiu1997.mxrandom.loader

import com.muxiu1997.mxrandom.MODID
import com.muxiu1997.mxrandom.MXRandom.MTE_ID_OFFSET
import com.muxiu1997.mxrandom.metatileentity.LargeMolecularAssembler
import cpw.mods.fml.common.event.FMLInitializationEvent
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.util.StatCollector

object GTMetaTileEntityLoader {
    lateinit var largeMolecularAssembler: LargeMolecularAssembler

    fun load(@Suppress("UNUSED_PARAMETER") e: FMLInitializationEvent) {
        largeMolecularAssembler = register(1, "Large Molecular Assembler")
    }

    private inline fun <reified T : GT_MetaTileEntity_EnhancedMultiBlockBase<*>> register(
        id: Int, name: String, unlocalizedName: String? = null
    ): T {
        val constructor = T::class.java.getConstructor(Int::class.java, String::class.java, String::class.java)
        val metaTileEntityUnlocalizedName = MODID + (unlocalizedName ?: name.replace(" ", "").lowercase())
        return constructor.newInstance(
            MTE_ID_OFFSET + id,
            metaTileEntityUnlocalizedName,
            name
        )
    }
}
