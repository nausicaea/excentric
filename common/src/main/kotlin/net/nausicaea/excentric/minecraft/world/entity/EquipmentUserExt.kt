package net.nausicaea.excentric.minecraft.world.entity

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentUser
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Some
import net.nausicaea.excentric.util.filter

fun EquipmentUser.getItemBySlot(slot: EquipmentSlot): Option<ItemStack> = Some(this.getItemBySlot(slot)).filter { s -> !s.isEmpty }
