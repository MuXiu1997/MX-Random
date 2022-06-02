package com.muxiu1997.mxrandom.network.container

import com.muxiu1997.mxrandom.metatileentity.LargeMolecularAssembler
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container

class ContainerConfigLargeMolecularAssembler(val LMA: LargeMolecularAssembler) : Container() {
    override fun canInteractWith(player: EntityPlayer?): Boolean {
        return true
    }
}
