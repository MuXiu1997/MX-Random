package com.muxiu1997.mxrandom.client.gui

import com.muxiu1997.mxrandom.MODID
import com.muxiu1997.mxrandom.MXRandom._info
import com.muxiu1997.mxrandom.metatileentity.LargeMolecularAssembler
import com.muxiu1997.mxrandom.network.container.ContainerConfigLargeMolecularAssembler
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.StatCollector
import java.awt.Color


class GuiConfigLargeMolecularAssembler(GuiContainer: ContainerConfigLargeMolecularAssembler) :
    GuiContainer(GuiContainer) {
    @Suppress("PrivatePropertyName")
    private val LMA: LargeMolecularAssembler = GuiContainer.LMA
    private lateinit var buttonToggleCraftingFX: GuiButton

    init {
        xSize = 176
        ySize = 107
    }

    override fun initGui() {
        super.initGui()
        buttonToggleCraftingFX =
            GuiButton(
                0,
                guiLeft + xSize - BUTTON_WIDTH - PADDING,
                guiTop + PADDING + 16,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                buttonToggleCraftingFXDisplayString()
            )
        buttonList.add(buttonToggleCraftingFX)
    }

    override fun updateScreen() {
        super.updateScreen()
        buttonToggleCraftingFX.displayString = buttonToggleCraftingFXDisplayString()
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        this.mc.textureManager.bindTexture(BACKGROUND_TEXTURE)
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        fontRendererObj.drawString(LMA.localName, PADDING, PADDING, Color.BLACK.rgb)
        fontRendererObj.drawString(LANG_CRAFTING_FX, PADDING, PADDING + 20, Color.BLACK.rgb)
    }

    override fun actionPerformed(button: GuiButton?) {
        button ?: return
        when (button) {
            buttonToggleCraftingFX -> {
                LMA.hiddenCraftingFX = !LMA.hiddenCraftingFX
                LMA.applyConfigChanges()
            }
        }
    }

    private fun buttonToggleCraftingFXDisplayString(): String {
        return if (LMA.hiddenCraftingFX) {
            LANG_HIDDEN
        } else {
            LANG_VISIBLE
        }
    }

    companion object {
        val BACKGROUND_TEXTURE = ResourceLocation(MODID, "textures/gui/configMetaTileEntity.png")
        const val PADDING = 8
        const val BUTTON_WIDTH = 40
        const val BUTTON_HEIGHT = 18

        val LANG_CRAFTING_FX: String
            get() = StatCollector.translateToLocal("$MODID.client.gui.GuiConfigLargeMolecularAssembler.craftingFX")
        val LANG_HIDDEN: String
            get() = StatCollector.translateToLocal("$MODID.client.gui.GuiConfigLargeMolecularAssembler.hidden")
        val LANG_VISIBLE: String
            get() = StatCollector.translateToLocal("$MODID.client.gui.GuiConfigLargeMolecularAssembler.visible")
    }
}
