package net.nausicaea.excentric.minecraft.world.entity

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentUser
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.util.Opt
import net.nausicaea.excentric.util.asOpt

fun EquipmentUser.getNonemptyItemBySlot(slot: EquipmentSlot): Opt<ItemStack> =
    this.getItemBySlot(slot).asOpt().filter {
        !it.isEmpty
    }
