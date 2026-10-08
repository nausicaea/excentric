package net.nausicaea.excentric.minecraft.world

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.java.util.stream.Enumerated
import net.nausicaea.excentric.java.util.stream.enumerate
import java.util.stream.Stream

fun SimpleContainer.stream(): Stream<Enumerated<ItemStack>> =
    this.items
        .stream()
        .enumerate()
        .filter { stack: Enumerated<ItemStack> -> !stack.value.isEmpty }
