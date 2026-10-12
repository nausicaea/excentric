package net.nausicaea.excentric.minecraft.world.entity

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentUser
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.util.filter

fun EquipmentUser.getNonemptyItemBySlot(slot: EquipmentSlot): ItemStack? = this.getItemBySlot(slot).filter { !it.isEmpty }
