package com.muxiu1997.mxrandom

import gregtech.api.interfaces.metatileentity.IMetaTileEntity
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import net.minecraft.world.World

// region net.minecraft.world.World
@JvmName("getMetaTileEntity")
fun World.getMetaTileEntity(x: Int, y: Int, z: Int): IMetaTileEntity? {
    val baseMetaTileEntity = this.getTileEntity(x, y, z) as? IGregTechTileEntity ?: return null
    return baseMetaTileEntity.metaTileEntity
}

@JvmName("getMetaTileEntityByClass")
inline fun <reified T : IMetaTileEntity> World.getMetaTileEntity(x: Int, y: Int, z: Int): T? {
    return this.getMetaTileEntity(x, y, z) as? T
}
// endregion
