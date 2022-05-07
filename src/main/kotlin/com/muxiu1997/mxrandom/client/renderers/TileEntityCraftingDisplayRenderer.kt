package com.muxiu1997.mxrandom.client.renderers

import com.muxiu1997.mxrandom.blocks.TileEntityCraftingDisplay
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11

object TileEntityCraftingDisplayRenderer : TileEntitySpecialRenderer() {
    override fun renderTileEntityAt(
        te: TileEntity?,
        x: Double,
        y: Double,
        z: Double,
        t: Float
    ) {
        if (te !is TileEntityCraftingDisplay || te.worldObj == null) return
        val itemStack = te.itemStack ?: return
        GL11.glPushMatrix()

        val ticks: Float = Minecraft.getMinecraft().renderViewEntity.ticksExisted.toFloat() + t
        val h = MathHelper.sin(ticks % 32767.0f / 16.0f) * 0.05f
        GL11.glTranslatef(x.toFloat() + 0.5f, y.toFloat() + 0.15f + h, z.toFloat() + 0.5f)
        GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f)
        if (itemStack.item is ItemBlock) {
            GL11.glScalef(4.0f, 4.0f, 4.0f)
        } else {
            GL11.glScalef(2.0f, 2.0f, 2.0f)
        }

        val entityItem = EntityItem(te.getWorldObj(), 0.0, 0.0, 0.0, itemStack)
        entityItem.hoverStart = 0.0f
        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0, 0.0, 0.0, 0.0f, 0.0f)

        GL11.glPopMatrix()
    }

}
