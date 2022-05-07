package com.muxiu1997.mxrandom.blocks

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World

object BlockCraftingDisplay : Block(MaterialAir), ITileEntityProvider {
    override fun createNewTileEntity(w: World?, m: Int): TileEntity = TileEntityCraftingDisplay()

    override fun getRenderType(): Int {
        return -1
    }

    override fun isOpaqueCube(): Boolean {
        return false
    }


    override fun canCollideCheck(p_149678_1_: Int, p_149678_2_: Boolean): Boolean {
        return false
    }

    override fun dropBlockAsItemWithChance(
        p_149690_1_: World?,
        p_149690_2_: Int,
        p_149690_3_: Int,
        p_149690_4_: Int,
        p_149690_5_: Int,
        p_149690_6_: Float,
        p_149690_7_: Int
    ) {
    }

    override fun getCollisionBoundingBoxFromPool(
        p_149668_1_: World?,
        p_149668_2_: Int,
        p_149668_3_: Int,
        p_149668_4_: Int
    ): AxisAlignedBB? = null

    object MaterialAir : Material(MapColor.airColor) {
        init {
            setReplaceable()
        }

        override fun isSolid(): Boolean {
            return false
        }

        override fun getCanBlockGrass(): Boolean {
            return false
        }

        override fun blocksMovement(): Boolean {
            return false
        }
    }
}
