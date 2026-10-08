package net.nausicaea.excentric.minecraft.world

import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.java.util.stream.StreamUtils
import net.nausicaea.excentric.java.util.stream.StreamUtils.Enumerated
import java.util.stream.Stream

fun SimpleContainer.stream(): Stream<Enumerated<ItemStack>> =
    StreamUtils
        .enumerate(this.items.stream())
        .filter { stack: Enumerated<ItemStack> -> !stack.value.isEmpty }
