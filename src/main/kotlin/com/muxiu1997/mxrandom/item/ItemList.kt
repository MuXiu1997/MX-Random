package com.muxiu1997.mxrandom.item

import gregtech.api.util.GT_Utility
import net.minecraft.item.ItemStack


enum class ItemList {
    /** @see com.muxiu1997.mxrandom.metatileentity.GT_TileEntity_LargeMolecularAssembler */
    LARGE_MOLECULAR_ASSEMBLER,
    ;

    private var itemStack: ItemStack? = null
    private var hasBeenSet = false

    fun set(itemStack: ItemStack) {
        this.hasBeenSet = true
        this.itemStack = GT_Utility.copyAmount(1, itemStack)
    }

    fun get(amount: Long): ItemStack {
        if (!hasBeenSet) throw IllegalAccessError("The Enum '$name' has not been set to an Item at this time!")
        return GT_Utility.copyAmount(amount, itemStack)
    }
}
