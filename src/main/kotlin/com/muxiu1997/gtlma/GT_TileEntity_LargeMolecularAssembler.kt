@file:Suppress("ClassName")

package com.muxiu1997.gtlma

import appeng.api.networking.GridFlags
import appeng.api.networking.IGridNode
import appeng.api.networking.crafting.ICraftingPatternDetails
import appeng.api.networking.crafting.ICraftingProvider
import appeng.api.networking.crafting.ICraftingProviderHelper
import appeng.api.networking.events.MENetworkCraftingPatternChange
import appeng.api.networking.security.IActionHost
import appeng.api.util.DimensionalCoord
import appeng.items.misc.ItemEncodedPattern
import appeng.me.GridAccessException
import appeng.me.helpers.AENetworkProxy
import appeng.me.helpers.IGridProxyable
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility
import gregtech.api.GregTech_API
import gregtech.api.enums.ItemList
import gregtech.api.enums.Textures.BlockIcons
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import gregtech.api.render.TextureFactory
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_StructureUtility
import gregtech.api.util.IGT_HatchAdder
import gregtech.common.items.behaviors.Behaviour_DataOrb
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.ForgeDirection
import java.util.*

class GT_TileEntity_LargeMolecularAssembler :
    GT_MetaTileEntity_EnhancedMultiBlockBase<GT_TileEntity_LargeMolecularAssembler>, ICraftingProvider,
    IActionHost, IGridProxyable {

    private var cachedDataOrb: ItemStack? = null
    private var cachedAeJobs: LinkedList<ItemStack>? = LinkedList()
    private var aeJobsDirty = false

    private var cachedPatterns: List<ItemStack> = emptyList()
    private var cachedPatternDetails: List<ICraftingPatternDetails> = emptyList()

    constructor(aID: Int, aName: String, aNameRegional: String) : super(aID, aName, aNameRegional)

    constructor(aName: String) : super(aName)

    private inline fun withAeJobs(action: (dataOrb: ItemStack, aeJobs: LinkedList<ItemStack>) -> Unit) {
        if (mInventory[1] === cachedDataOrb) {
            action(cachedDataOrb!!, cachedAeJobs!!)
            return
        }
        if (!ItemList.Tool_DataOrb.isStackEqual(mInventory[1], false, true)) {
            cachedDataOrb = null
            cachedAeJobs = null
            return
        }
        val dataOrb = mInventory[1]
        var dataTitle = Behaviour_DataOrb.getDataTitle(dataOrb)
        if (dataTitle.isEmpty()) {
            dataTitle = DATA_ORB_TITLE
            Behaviour_DataOrb.setDataTitle(dataOrb, dataTitle)
            Behaviour_DataOrb.setNBTInventory(dataOrb, emptyArray())
        }
        if (dataTitle != DATA_ORB_TITLE) {
            cachedDataOrb = null
            cachedAeJobs = null
            return
        }
        cachedDataOrb = dataOrb
        cachedAeJobs = Behaviour_DataOrb.getNBTInventory(dataOrb).toCollection(LinkedList())
        action(cachedDataOrb!!, cachedAeJobs!!)
    }


    //region GT_MetaTileEntity_EnhancedMultiBlockBase
    override fun newMetaEntity(iGregTechTileEntity: IGregTechTileEntity): IMetaTileEntity {
        return GT_TileEntity_LargeMolecularAssembler(this.mName)
    }

    override fun getTexture(
        aBaseMetaTileEntity: IGregTechTileEntity?,
        aSide: Byte,
        aFacing: Byte,
        aColorIndex: Byte,
        aActive: Boolean,
        aRedstone: Boolean
    ): Array<ITexture> {
        // TODO: add textures
        return if (aSide == aFacing) {
            if (aActive) arrayOf(
                BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE).extFacing()
                    .build(),
                TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW).extFacing()
                    .glow().build()
            ) else arrayOf(
                BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER).extFacing().build(),
                TextureFactory.builder().addIcon(BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW).extFacing().glow()
                    .build()
            )
        } else arrayOf(BlockIcons.getCasingTextureForId(CASING_INDEX))
    }

    override fun isCorrectMachinePart(aStack: ItemStack?): Boolean = true

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        withAeJobs { _, aeJobs ->
            mEUt = -16
            // TODO: calc times
            val times = 2
            val outputs = LinkedList<ItemStack>()
            for (i in 0 until times) {
                if (aeJobs.isNotEmpty()) {
                    outputs.addFirst(aeJobs.removeFirst())
                    aeJobsDirty = true
                } else {
                    break
                }
            }
            if (outputs.isNotEmpty()) {
                mEUt = -64
                mOutputItems = outputs.toTypedArray()
            }

            mEfficiency = 10000 - (idealStatus - repairStatus) * 1000
            mEfficiencyIncrease = 10000
            mMaxProgresstime = 20
            return true
        }
        return false
    }

    override fun saveNBTData(aNBT: NBTTagCompound) {
        super.saveNBTData(aNBT)
        saveAeJobsIfNeeded()
    }

    override fun checkMachine(aBaseMetaTileEntity: IGregTechTileEntity?, aStack: ItemStack?): Boolean {
        // TODO: check machine
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 1, 2, 0)) return false
        return true
    }

    override fun getMaxEfficiency(aStack: ItemStack?): Int = 10000

    override fun getDamageToComponent(aStack: ItemStack?): Int = 0

    override fun explodesOnComponentBreak(aStack: ItemStack?): Boolean = false


    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        // TODO: add tooltip
        val tt = GT_Multiblock_Tooltip_Builder()
        tt.addMachineType("Molecular Assembler")
        return tt
    }

    override fun construct(itemStack: ItemStack?, b: Boolean) {
        buildPiece(STRUCTURE_PIECE_MAIN, itemStack, b, 1, 2, 0)
    }

    override fun getStructureDefinition(): IStructureDefinition<GT_TileEntity_LargeMolecularAssembler> =
        STRUCTURE_DEFINITION

    //endregion

    override fun onPostTick(aBaseMetaTileEntity: IGregTechTileEntity, aTick: Long) {
        super.onPostTick(aBaseMetaTileEntity, aTick)
        if (aBaseMetaTileEntity.isServerSide) {
            syncAEProxyActive(aBaseMetaTileEntity)
            saveAeJobsIfNeeded()
            issuePatternChangeIfNeeded()
        }
    }

    private fun saveAeJobsIfNeeded() {
        if (!aeJobsDirty) return
        withAeJobs { dataOrb, aeJobs ->
            Behaviour_DataOrb.setNBTInventory(dataOrb, aeJobs.toTypedArray())
            aeJobsDirty = false
        }
    }

    private fun issuePatternChangeIfNeeded() {
        compactedInputs.let { inputs ->
            val patterns = inputs.filter { it.item is ItemEncodedPattern }
            if (patterns == cachedPatterns) return
            cachedPatterns = patterns
            cachedPatternDetails = patterns.map {
                (it.item as ItemEncodedPattern).getPatternForItem(it, baseMetaTileEntity.world)
            }
            proxy?.let {
                try {
                    it.grid.postEvent(MENetworkCraftingPatternChange(this, it.node))
                } catch (ignored: GridAccessException) {
                }
            }
        }
    }

    private fun syncAEProxyActive(aBaseMetaTileEntity: IGregTechTileEntity) {
        if (gridProxy == null) {
            gridProxy = AENetworkProxy(
                this, "proxy", this.getStackForm(1), true
            ).apply {
                setFlags(GridFlags.REQUIRE_CHANNEL)
                onReady()
            }
        }

        if (aBaseMetaTileEntity.isActive) {
            if (proxy?.isReady == false) proxy?.onReady()
        } else {
            if (proxy?.isReady == true) proxy?.invalidate()
        }
    }

    //region ICraftingProvider
    override fun pushPattern(patternDetails: ICraftingPatternDetails, table: InventoryCrafting): Boolean {
        withAeJobs { _, aeJobs ->
            if (aeJobs.size < 256) {
                aeJobs.add(patternDetails.getOutput(table, baseMetaTileEntity.world))
                aeJobsDirty = true
                return true
            }
        }
        return false
    }

    override fun isBusy(): Boolean {
        withAeJobs { _, aeJobs ->
            if (aeJobs.size < 256) return false
        }
        return true
    }

    override fun provideCrafting(craftingTracker: ICraftingProviderHelper) {
        if (proxy?.isReady == true) {
            cachedPatternDetails.forEach { craftingTracker.addCraftingOption(this, it) }
        }
    }
    //endregion

    //region IActionHost, IGridProxyable
    private var gridProxy: AENetworkProxy? = null

    override fun getProxy(): AENetworkProxy? {
        return gridProxy
    }

    override fun getGridNode(dir: ForgeDirection?): IGridNode? {
        return this.proxy?.node
    }

    override fun securityBreak() {
        baseMetaTileEntity.disableWorking()
    }

    override fun getLocation(): DimensionalCoord {
        return DimensionalCoord(
            baseMetaTileEntity.world,
            baseMetaTileEntity.xCoord,
            baseMetaTileEntity.yCoord.toInt(),
            baseMetaTileEntity.zCoord
        )
    }

    override fun getActionableNode(): IGridNode? {
        return this.proxy?.node
    }

    //endregion

    companion object {
        private const val CASING_INDEX = 49
        private const val STRUCTURE_PIECE_MAIN = "main"
        private const val DATA_ORB_TITLE = "AE-JOBS"
        private val STRUCTURE_DEFINITION =
            // TODO: StructureDefinition
            StructureDefinition.builder<GT_TileEntity_LargeMolecularAssembler>().addShape(
                STRUCTURE_PIECE_MAIN, StructureUtility.transpose(
                    arrayOf(
                        arrayOf("ccc", "ccc", "ccc"),
                        arrayOf("ccc", "ccc", "ccc"),
                        arrayOf("c~c", "ccc", "ccc")
                    )
                )
            ).addElement(
                'c', StructureUtility.ofChain(
                    GT_StructureUtility.ofHatchAdder(
                        hatchAdderOf(GT_TileEntity_LargeMolecularAssembler::addMaintenanceToMachineList),
                        CASING_INDEX,
                        1
                    ), GT_StructureUtility.ofHatchAdder(
                        hatchAdderOf(GT_TileEntity_LargeMolecularAssembler::addOutputToMachineList),
                        CASING_INDEX,
                        1
                    ), GT_StructureUtility.ofHatchAdder(
                        hatchAdderOf(GT_TileEntity_LargeMolecularAssembler::addInputToMachineList),
                        CASING_INDEX,
                        1
                    ), GT_StructureUtility.ofHatchAdder(
                        hatchAdderOf(GT_TileEntity_LargeMolecularAssembler::addEnergyInputToMachineList),
                        CASING_INDEX,
                        1
                    ), StructureUtility.onElementPass(
                        { }, StructureUtility.ofBlock(GregTech_API.sBlockCasings4, 1)
                    )
                )
            ).build()

        private fun hatchAdderOf(f: (GT_TileEntity_LargeMolecularAssembler).(IGregTechTileEntity, Int) -> Boolean): IGT_HatchAdder<GT_TileEntity_LargeMolecularAssembler> {
            return IGT_HatchAdder<GT_TileEntity_LargeMolecularAssembler> { t, iGregTechTileEntity, aShort ->
                f(t, iGregTechTileEntity, aShort.toInt())
            }
        }
    }
}
