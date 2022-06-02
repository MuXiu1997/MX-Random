package com.muxiu1997.mxrandom.loader

import appeng.api.AEApi
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import gregtech.api.enums.GT_Values
import gregtech.api.enums.Materials
import gregtech.api.enums.ItemList as GTItemList

object RecipeLoader {
    fun load(@Suppress("UNUSED_PARAMETER") e: FMLPostInitializationEvent) {
        GT_Values.RA.addAssemblerRecipe(
            arrayOf(
                AEApi.instance().definitions().blocks().iface().maybeStack(8).get(),
                AEApi.instance().definitions().blocks().molecularAssembler().maybeStack(8).get(),
                GTItemList.Emitter_IV.get(4),
                GTItemList.Casing_RobustTungstenSteel.get(1),
            ),
            Materials.Plastic.getMolten(1296),
            GTMetaTileEntityLoader.largeMolecularAssembler.getStackForm(1),
            4800,
            8000
        )
    }
}
