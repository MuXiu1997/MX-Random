package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.item.ItemList
import com.muxiu1997.mxrandom.metatileentity.GT_TileEntity_LargeMolecularAssembler
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerBlocks() {
    }

    fun registerTileEntity() {
    }

    fun registerGTMetaTileEntity() {
        register<GT_TileEntity_LargeMolecularAssembler>(
            "largemolecularassembler",
            1,
            ItemList.LARGE_MOLECULAR_ASSEMBLER
        ) When always
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

        protected inline fun <reified T : GT_MetaTileEntity_EnhancedMultiBlockBase<*>> register(
            name: String, id: Int, ref: ItemList
        ): () -> Unit {
            return {
                val constructor = T::class.java.getConstructor(Int::class.java, String::class.java, String::class.java)
                val mte = constructor.newInstance(
                    MXRandom.MTE_ID_OFFSET + id,
                    "$MODID.$name",
                    StatCollector.translateToLocal("tile.$MODID.$name.name")
                )
                ref.set(mte.getStackForm(1))
            }
        }
        // endregion
    }
}
