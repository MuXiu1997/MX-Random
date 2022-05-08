package com.muxiu1997.mxrandom

import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.MXRandom.MODNAME
import com.muxiu1997.mxrandom.proxy.CommonProxy
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger

@Mod(
    modid = MODID,
    name = MODNAME,
    version = "", // Handled by gradle
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    dependencies = "" +
        "required-after:forgelin;" +
        "required-after:appliedenergistics2;" +
        "required-after:gregtech",
)
object MXRandom {
    const val MODID = "mxrandom"
    const val MODNAME = "MX-Random"

    const val MTE_ID_OFFSET = 14100

    lateinit var logger: Logger

    @SidedProxy(
        serverSide = "com.muxiu1997.mxrandom.proxy.CommonProxy",
        clientSide = "com.muxiu1997.mxrandom.proxy.ClientProxy"
    )
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        logger = e.modLog
    }

    @Mod.EventHandler
    fun init(@Suppress("UNUSED_PARAMETER") e: FMLInitializationEvent) {
        proxy.registerBlocks()
        proxy.registerTileEntity()
        proxy.registerGTMetaTileEntity()
        proxy.registerRenderers()
    }
}


