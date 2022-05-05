@file:Suppress("ClassName")

package com.muxiu1997.gtlma.tileentities

import appeng.api.AEApi
import appeng.api.networking.GridFlags
import appeng.api.networking.IGridNode
import appeng.api.networking.crafting.ICraftingPatternDetails
import appeng.api.networking.crafting.ICraftingProvider
import appeng.api.networking.crafting.ICraftingProviderHelper
import appeng.api.networking.events.MENetworkCraftingPatternChange
import appeng.api.networking.security.BaseActionSource
import appeng.api.networking.security.IActionHost
import appeng.api.networking.security.MachineSource
import appeng.api.storage.data.IAEItemStack
import appeng.api.storage.data.IItemList
import appeng.api.util.DimensionalCoord
import appeng.items.misc.ItemEncodedPattern
import appeng.me.GridAccessException
import appeng.me.helpers.AENetworkProxy
import appeng.me.helpers.IGridProxyable
import appeng.util.Platform
import com.gtnewhorizon.structurelib.structure.IStructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureDefinition
import com.gtnewhorizon.structurelib.structure.StructureUtility.*
import com.muxiu1997.gtlma.GtLargeMolecularAssembler
import com.muxiu1997.gtlma.GtLargeMolecularAssembler.MODNAME
import gregtech.api.GregTech_API
import gregtech.api.enums.ItemList
import gregtech.api.enums.Textures.BlockIcons
import gregtech.api.interfaces.ITexture
import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase
import gregtech.api.render.TextureFactory
import gregtech.api.util.GT_Multiblock_Tooltip_Builder
import gregtech.api.util.GT_StructureUtility.ofHatchAdder
import gregtech.api.util.GT_Utility
import gregtech.common.items.behaviors.Behaviour_DataOrb
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.ForgeDirection
import java.util.*
import kotlin.math.max

class GT_TileEntity_LargeMolecularAssembler :
    GT_MetaTileEntity_EnhancedMultiBlockBase<GT_TileEntity_LargeMolecularAssembler>, ICraftingProvider,
    IActionHost, IGridProxyable {

    private var cachedDataOrb: ItemStack? = null
    private var cachedAeJobs: LinkedList<ItemStack>? = LinkedList()
    private var aeJobsDirty = false

    private var cachedPatterns: List<ItemStack> = emptyList()
    private var cachedPatternDetails: List<ICraftingPatternDetails> = emptyList()

    private var requestSource: BaseActionSource? = null
    private var cachedOutputs = AEApi.instance().storage().createItemList()
    private var lastOutputFailed = false
    private var lastOutputTick: Long = 0
    private var tickCounter: Long = 0

    constructor(aID: Int, aName: String, aNameRegional: String) : super(aID, aName, aNameRegional)

    constructor(aName: String) : super(aName)


    // region GT_MetaTileEntity_EnhancedMultiBlockBase
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
        return if (aSide == aFacing) {
            arrayOf(
                BlockIcons.getCasingTextureForId(CASING_INDEX),
                TextureFactory.builder().addIcon(BlockIcons.OVERLAY_ME_HATCH).extFacing().build(),
            )
        } else arrayOf(
            BlockIcons.getCasingTextureForId(CASING_INDEX),
        )
    }

    override fun isCorrectMachinePart(aStack: ItemStack?): Boolean = true

    override fun checkRecipe(aStack: ItemStack?): Boolean {
        withAeJobs { _, aeJobs ->
            mMaxProgresstime = 20
            var craftingProgressTime = 20
            var craftingEUt = EU_PER_TICK_CRAFTING
            mEUt = -EU_PER_TICK_BASIC
            var extraTier = max(0, GT_Utility.getTier(maxInputVoltage).toInt() - 2)
            for (i in 0 until 2) {
                if (extraTier <= 0) break
                craftingProgressTime /= 2
                craftingEUt *= 4
                extraTier--
            }
            var times = 2
            while (times < 256) {
                if (extraTier <= 0) break
                times *= 2
                craftingEUt *= 4
                extraTier--
            }
            val outputs = LinkedList<ItemStack>()
            for (i in 0 until times) {
                if (aeJobs.isEmpty()) break
                outputs.addFirst(aeJobs.removeFirst())
                aeJobsDirty = true
            }
            if (outputs.isNotEmpty()) {
                mEUt = -craftingEUt
                mMaxProgresstime = craftingProgressTime
                mOutputItems = outputs.toTypedArray()
            }
            mEfficiency = 10000 - (idealStatus - repairStatus) * 1000
            mEfficiencyIncrease = 10000
            return true
        }
        return false
    }

    override fun saveNBTData(nbt: NBTTagCompound) {
        saveAeJobsIfNeeded()
        super.saveNBTData(nbt)
        cachedOutputs.saveNBTData(nbt, CACHED_OUTPUTS_NBT_KEY)
    }

    override fun loadNBTData(aNBT: NBTTagCompound) {
        super.loadNBTData(aNBT)
        cachedOutputs.loadNBTData(aNBT, CACHED_OUTPUTS_NBT_KEY)
    }

    override fun getMaxEfficiency(aStack: ItemStack?): Int = 10000

    override fun getDamageToComponent(aStack: ItemStack?): Int = 0

    override fun explodesOnComponentBreak(aStack: ItemStack?): Boolean = false

    override fun createTooltip(): GT_Multiblock_Tooltip_Builder {
        return GT_Multiblock_Tooltip_Builder().also {
            it.addMachineType(MACHINE_TYPE)
                .addInfo("Basic: $EU_PER_TICK_BASIC Eu/t, Unaffected by overclocking")
                .addInfo("Crafting: $EU_PER_TICK_CRAFTING Eu/t")
                .addSeparator()
                .beginStructureBlock(5, 5, 5, true)
                .addController("Front center")
                .addInputBus("Any casing", 1)
                .addEnergyHatch("Any casing", 1)
                .addMaintenanceHatch("Any casing", 1)
                .toolTipFinisher(MODNAME)
        }
    }

    override fun checkMachine(aBaseMetaTileEntity: IGregTechTileEntity?, aStack: ItemStack?): Boolean {
        if (
            !checkPiece(STRUCTURE_PIECE_MAIN, 2, 4, 0) ||
            !checkHatches()
        ) return false
        return true
    }

    private fun checkHatches(): Boolean {
        if (mMaintenanceHatches.size != 1 || mEnergyHatches.isEmpty()) return false
        return true
    }

    override fun construct(itemStack: ItemStack?, hintsOnly: Boolean) {
        buildPiece(STRUCTURE_PIECE_MAIN, itemStack, hintsOnly, 2, 4, 0)
    }

    override fun getStructureDefinition(): IStructureDefinition<GT_TileEntity_LargeMolecularAssembler> =
        STRUCTURE_DEFINITION

    override fun addOutput(stack: ItemStack): Boolean {
        cachedOutputs.add(AEApi.instance().storage().createItemStack(stack))
        return true
    }

    override fun onPostTick(baseMetaTileEntity: IGregTechTileEntity, tick: Long) {
        super.onPostTick(baseMetaTileEntity, tick)
        if (baseMetaTileEntity.isServerSide) {
            flushCachedOutputsIfNeeded(tick)
            saveAeJobsIfNeeded()
            syncAEProxyActive(baseMetaTileEntity)
            issuePatternChangeIfNeeded()
        }
    }
    // endregion

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
        cachedAeJobs = Behaviour_DataOrb.getNBTInventory(dataOrb).filterNotNull().toCollection(LinkedList())
        action(cachedDataOrb!!, cachedAeJobs!!)
    }

    private fun getRequest(): BaseActionSource? {
        if (requestSource == null) requestSource = MachineSource(baseMetaTileEntity as IActionHost)
        return requestSource
    }

    private fun flushCachedOutputsIfNeeded(tick: Long) {
        tickCounter = tick
        if (tickCounter <= lastOutputTick + 40) return

        lastOutputFailed = true
        proxy?.let {
            try {
                val storage = it.storage.itemInventory
                for (s in cachedOutputs) {
                    if (s.stackSize == 0L) continue
                    val rest = Platform.poweredInsert(it.energy, storage, s, getRequest())
                    if (rest != null && rest.stackSize > 0) {
                        lastOutputFailed = true
                        s.stackSize = rest.stackSize
                        break
                    }
                    s.stackSize = 0
                }
            } catch (ignored: GridAccessException) {
                lastOutputFailed = true
            }
        }
        lastOutputTick = tickCounter
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
            val patterns = inputs.filter {
                it.item is ItemEncodedPattern &&
                    (it.item as ItemEncodedPattern).getPatternForItem(it, baseMetaTileEntity.world).isCraftable
            }
            if (patterns == cachedPatterns) return
            cachedPatterns = patterns
            cachedPatternDetails = patterns.map {
                (it.item as ItemEncodedPattern).getPatternForItem(it, baseMetaTileEntity.world)
            }
            proxy?.let {
                try {
                    it.grid.postEvent(MENetworkCraftingPatternChange(this, it.node))
                } catch (ignored: GridAccessException) {
                    // Do nothing
                }
            }
        }
    }

    private fun syncAEProxyActive(baseMetaTileEntity: IGregTechTileEntity) {
        if (gridProxy == null) {
            gridProxy = AENetworkProxy(
                this, "proxy", this.getStackForm(1), true
            ).apply {
                setFlags(GridFlags.REQUIRE_CHANNEL)
                onReady()
            }
        }

        proxy?.run {
            if (baseMetaTileEntity.isActive) {
                if (!isReady) onReady()
            } else {
                if (isReady) invalidate()
            }
        }
    }

    private fun addToLargeMolecularAssemblerList(tileEntity: IGregTechTileEntity?, baseCasingIndex: Short): Boolean {
        return sequenceOf(
            this::addMaintenanceToMachineList,
            this::addInputToMachineList,
            this::addEnergyInputToMachineList,
        ).any {
            it(tileEntity, baseCasingIndex.toInt())
        }
    }

    // region ICraftingProvider
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
    // endregion

    // region IActionHost, IGridProxyable
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
    // endregion

    companion object {
        const val MTE_ID = GtLargeMolecularAssembler.MTE_ID_OFFSET + 1
        private const val MACHINE_TYPE = "Molecular Assembler"
        private const val EU_PER_TICK_BASIC = 16
        private const val EU_PER_TICK_CRAFTING = 64
        private const val CASING_INDEX = 48
        private const val DATA_ORB_TITLE = "AE-JOBS"
        private const val CACHED_OUTPUTS_NBT_KEY = "cachedOutputs"
        private const val STRUCTURE_PIECE_MAIN = "main"
        // region STRUCTURE_DEFINITION
        private val STRUCTURE_DEFINITION =
            StructureDefinition.builder<GT_TileEntity_LargeMolecularAssembler>()
                .addShape(
                    STRUCTURE_PIECE_MAIN, transpose(
                        arrayOf(
                            arrayOf("CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"),
                            arrayOf("CGGGC", "G---G", "G---G", "G---G", "CGGGC"),
                            arrayOf("CGGGC", "G---G", "G---G", "G---G", "CGGGC"),
                            arrayOf("CGGGC", "G---G", "G---G", "G---G", "CGGGC"),
                            arrayOf("CC~CC", "CGGGC", "CGGGC", "CGGGC", "CCCCC"),
                        )
                    )
                )
                .addElement(
                    'C', ofChain(
                        ofHatchAdder(
                            GT_TileEntity_LargeMolecularAssembler::addToLargeMolecularAssemblerList,
                            CASING_INDEX,
                            1
                        ),
                        onElementPass(
                            { }, ofBlock(GregTech_API.sBlockCasings4, 0)
                        ),
                    )
                )
                .addElement(
                    'G',
                    ofBlockAnyMeta(AEApi.instance().definitions().blocks().quartzVibrantGlass().maybeBlock().get())
                )
                .build()
        //endregion

        private fun IItemList<IAEItemStack>.saveNBTData(nbt: NBTTagCompound, key: String) {
            val itemList = NBTTagList()
            this.forEach {
                if (it.stackSize <= 0) return@forEach
                val itemTag = NBTTagCompound()
                it.writeToNBT(itemTag)
                itemList.appendTag(itemTag)
            }
            nbt.setTag(key, itemList)
        }

        private fun IItemList<IAEItemStack>.loadNBTData(nbt: NBTTagCompound, key: String) {
            val itemList = nbt.getTag(key)
            if (itemList is NBTTagList) {
                repeat(itemList.tagCount()) { idx ->
                    this.add(
                        AEApi.instance().storage().createItemStack(GT_Utility.loadItem(itemList.getCompoundTagAt(idx)))
                    )
                }
            }
        }
    }
}
