package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.loader.GTMetaTileEntityLoader
import com.muxiu1997.mxrandom.loader.NetworkLoader
import com.muxiu1997.mxrandom.loader.RecipeLoader
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent


open class CommonProxy {
    fun onInit(e: FMLInitializationEvent) {
        GTMetaTileEntityLoader.load(e)
        NetworkLoader.load(e)
    }

    fun onPostInit(e: FMLPostInitializationEvent) {
        RecipeLoader.load(e)
    }
}
