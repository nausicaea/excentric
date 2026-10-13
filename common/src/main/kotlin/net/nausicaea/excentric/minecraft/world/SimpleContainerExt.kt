package net.nausicaea.excentric.minecraft.world

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

fun SimpleContainer.asSequence(): Sequence<IndexedValue<ItemStack>> =
    this.items
        .asSequence()
        .withIndex()
        .filter { !it.value.isEmpty }
