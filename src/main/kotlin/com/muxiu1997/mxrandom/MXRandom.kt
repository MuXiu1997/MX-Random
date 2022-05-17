package com.muxiu1997.mxrandom

import com.muxiu1997.mxrandom.loader.RecipeLoader
import com.muxiu1997.mxrandom.proxy.CommonProxy
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(
    modid = MODID,
    name = MODNAME,
    version = VERSION,
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
    dependencies = "" +
        "required-after:forgelin;" +
        "required-after:appliedenergistics2;" +
        "required-after:gregtech",
)
object MXRandomMod {
    @SidedProxy(
        serverSide = "$GROUPNAME.proxy.CommonProxy",
        clientSide = "$GROUPNAME.proxy.ClientProxy"
    )
    lateinit var proxy: CommonProxy

    @Mod.EventHandler
    fun init(@Suppress("UNUSED_PARAMETER") e: FMLInitializationEvent) {
        proxy.registerNetwork()
        proxy.registerBlocks()
        proxy.registerTileEntity()
        proxy.registerGTMetaTileEntity()
        proxy.registerRenderers()
    }

    @Mod.EventHandler
    fun postInit(@Suppress("UNUSED_PARAMETER") e: FMLPostInitializationEvent) {
        RecipeLoader.run()
    }
}

@Suppress("unused", "MemberVisibilityCanBePrivate", "FunctionName")
object MXRandom {
    // region Logger
    val logger: Logger = LogManager.getLogger(MODID)
    fun debug(message: Any) {
        logger.debug("[$MODNAME]$message")
    }

    fun info(message: Any) {
        logger.info("[$MODNAME]$message")
    }

    fun warn(message: Any) {
        logger.warn("[$MODNAME]$message")
    }

    fun error(message: Any) {
        logger.error("[$MODNAME]$message")
    }

    fun Any._debug() = debug(this)

    fun Any._info() = info(this)

    fun Any._warn() = warn(this)

    fun Any._error() = error(this)
    // endregion

    const val MTE_ID_OFFSET = 14100

    val network: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID)
}
