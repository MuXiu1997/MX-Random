package com.muxiu1997.gtlma

import com.muxiu1997.gtlma.GtLargeMolecularAssembler.MODID
import com.muxiu1997.gtlma.GtLargeMolecularAssembler.MODNAME
import com.muxiu1997.gtlma.proxy.CommonProxy
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import org.apache.logging.log4j.Logger

@Mod(
    modid = MODID,
    name = MODNAME,
    version = "0.0.1",
    dependencies = "required-after:forgelin;required-after:appliedenergistics2;",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object GtLargeMolecularAssembler {
    const val MODID = "GtLargeMolecularAssembler"
    const val MODNAME = "GtLargeMolecularAssembler"

    const val MTE_ID_OFFSET = 14100

    lateinit var logger: Logger

    @SidedProxy(
        serverSide = "com.muxiu1997.gtlma.proxy.CommonProxy",
        clientSide = "com.muxiu1997.gtlma.proxy.ClientProxy"
    )
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.modLog
    }

    @Mod.EventHandler
    fun init(@Suppress("UNUSED_PARAMETER") init: FMLInitializationEvent) {
        proxy.registerTileEntity()
    }
}


