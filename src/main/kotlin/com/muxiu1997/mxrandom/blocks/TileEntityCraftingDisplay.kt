package com.muxiu1997.mxrandom.blocks

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB


class TileEntityCraftingDisplay : TileEntity() {
    var itemStack: ItemStack? = null
        set(value) {
            field = value
            if (!worldObj.isRemote) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
            }
            markDirty()
        }

    var parent: IGregTechTileEntity? = null

    override fun updateEntity() {
        super.updateEntity()
        if (worldObj.isRemote) return
        if (parent == null || parent!!.isDead || !parent!!.isActive) {
            invalidate()
            if (worldObj.getBlock(xCoord, yCoord, zCoord) == blockType) {
                worldObj.setBlockToAir(xCoord, yCoord, zCoord)
            }
            markDirty()
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB = AxisAlignedBB.getBoundingBox(
        (xCoord - 1).toDouble(),
        (yCoord - 1).toDouble(),
        (zCoord - 1).toDouble(),
        (xCoord + 2).toDouble(),
        (yCoord + 2).toDouble(),
        (zCoord + 2).toDouble()
    )

    override fun readFromNBT(nbt: NBTTagCompound?) {
        super.readFromNBT(nbt)
        nbt ?: return
        if (nbt.hasKey("isEmpty") && nbt.getBoolean("isEmpty")) {
            itemStack = null
            return
        }
        if (nbt.hasKey("itemStack")) {
            itemStack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("itemStack"))
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound?) {
        super.writeToNBT(nbt)
        nbt ?: return
        itemStack?.let {
            nbt.setTag("itemStack", it.writeToNBT(NBTTagCompound()))
        }
        nbt.setBoolean("isEmpty", itemStack == null)
    }

    override fun getDescriptionPacket(): Packet {
        val nbt = NBTTagCompound()
        writeToNBT(nbt)
        return S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbt)
    }

    override fun onDataPacket(net: NetworkManager?, pkt: S35PacketUpdateTileEntity) {
        super.onDataPacket(net, pkt)
        readFromNBT(pkt.func_148857_g())
    }
}
