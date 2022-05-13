package com.muxiu1997.mxrandom.client.fx

import appeng.api.storage.data.IAEItemStack
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.EntityFX
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemBlock
import net.minecraft.util.MathHelper
import net.minecraft.world.World
import org.lwjgl.opengl.GL11

class CraftingFX(
    w: World, x: Double, y: Double, z: Double, age: Int, itemStack: IAEItemStack
) : EntityFX(w, x, y, z, 0.0, 0.0, 0.0) {
    private val entityItem = EntityItem(worldObj, 0.0, 0.0, 0.0, itemStack.itemStack).also { it.hoverStart = 0.0f }
    private val isItemBlock = itemStack.itemStack.item is ItemBlock

    init {
        motionX = 0.0
        motionY = 0.0
        motionZ = 0.0
        particleMaxAge = age + 1
        noClip = true
    }

    override fun renderParticle(
        tess: Tessellator, renderPartialTicks: Float, rX: Float, rY: Float, rZ: Float, rYZ: Float, rXY: Float
    ) {
        Tessellator.instance.draw()
        GL11.glPushMatrix()


        val ticks: Float = Minecraft.getMinecraft().renderViewEntity.ticksExisted.toFloat()
        val x = (prevPosX + (posX - prevPosX) * renderPartialTicks - interpPosX)
        val y = (prevPosY + (posY - prevPosY) * renderPartialTicks - interpPosY)
        val z = (prevPosZ + (posZ - prevPosZ) * renderPartialTicks - interpPosZ)
        val h = MathHelper.sin(ticks % 32767.0f / 16.0f) * 0.05f
        val scale = if (isItemBlock) 3.6f else 1.8f

        GL11.glTranslatef((x + 0.5).toFloat(), (y + 0.15f + h).toFloat(), (z + 0.5).toFloat())
        GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f)
        GL11.glScalef(scale, scale, scale)

        RenderManager.instance.renderEntityWithPosYaw(entityItem, 0.0, 0.0, 0.0, 0.0f, 0.0f)

        GL11.glPopMatrix()
        Tessellator.instance.startDrawingQuads()
    }

    override fun getBrightnessForRender(partialTickTime: Float): Int = 15.let { (it shl 20) or (it shl 4) }

    override fun getFXLayer(): Int = if (isItemBlock) 1 else 2

    override fun shouldRenderInPass(pass: Int): Boolean = pass == 2
}
