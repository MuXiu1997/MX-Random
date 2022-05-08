package com.muxiu1997.mxrandom.proxy

import com.muxiu1997.mxrandom.MXRandom
import com.muxiu1997.mxrandom.MXRandom.MODID
import com.muxiu1997.mxrandom.blocks.BlockCraftingDisplay
import com.muxiu1997.mxrandom.blocks.TileEntityCraftingDisplay
import com.muxiu1997.mxrandom.metatileentity.GT_TileEntity_LargeMolecularAssembler
import cpw.mods.fml.common.registry.GameRegistry
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.StatCollector


open class CommonProxy {
    fun registerBlocks() {
        BlockCraftingDisplay registerNamed "craftingDisplay" When always
    }

    fun registerTileEntity() {
        TileEntityCraftingDisplay::class.java registerNamed "craftingdisplay" When always
    }

    fun registerGTMetaTileEntity() {
        GT_TileEntity_LargeMolecularAssembler::class.java registerNamed "largemolecularassembler" withID 1 When always
    }

    open fun registerRenderers() {}


    companion object {
        // region :P
        const val always = true

        protected operator fun Boolean.plus(other: Boolean) = this && other

        @Suppress("FunctionName")
        infix fun (() -> Unit).When(conditions: Boolean) {
            if (conditions) {
                this()
            }
        }

        private infix fun Block.registerNamed(name: String): () -> Unit {
            return { GameRegistry.registerBlock(this, name) }
        }

        private infix fun Class<out GT_MetaTileEntity_EnhancedMultiBlockBase<*>>.registerNamed(name: String): (Int) -> Unit {
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

        private infix fun <T : Class<out TileEntity>> T.registerNamed(name: String): () -> Unit {
            return { GameRegistry.registerTileEntity(this, name) }
        }

        private fun Class<out TileEntity>.register(name: String): () -> Unit {
            return { GameRegistry.registerTileEntity(this, name) }
        }
        // endregion
    }
}
