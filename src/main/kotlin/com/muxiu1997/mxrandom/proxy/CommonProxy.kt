package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.metatileentity.GT_TileEntity_LargeMolecularAssembler
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerBlocks() {
    }

    fun registerTileEntity() {
    }

    fun registerGTMetaTileEntity() {
        cls<GT_TileEntity_LargeMolecularAssembler>() registerNamed "largemolecularassembler" withID 1 When always
    }

    open fun registerRenderers() {}


    companion object {
        // region :P
        protected const val always = true

        protected operator fun Boolean.plus(other: Boolean) = this && other

        @Suppress("FunctionName")
        protected infix fun (() -> Unit).When(conditions: Boolean) {
            if (conditions) {
                this()
            }
        }

        protected inline fun <reified T> cls(): Class<T> {
            return T::class.java
        }

        protected inline infix fun <reified T : GT_MetaTileEntity_EnhancedMultiBlockBase<*>> Class<T>.registerNamed(name: String): (Int) -> Unit {
            return { id ->
                this.getConstructor(Int::class.java, String::class.java, String::class.java).newInstance(
                    MXRandom.MTE_ID_OFFSET + id,
                    "$MODID.$name",
                    StatCollector.translateToLocal("tile.$MODID.$name.name")
                )
            }
        }

        private infix fun ((Int) -> Unit).withID(id: Int): () -> Unit {
            return { this(id) }
        }
        // endregion
    }
}
