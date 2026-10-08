package net.nausicaea.excentric.minecraft.core.component

import net.minecraft.core.component.DataComponentHolder
import net.minecraft.core.component.DataComponentType
import net.nausicaea.excentric.util.Option

fun <T> DataComponentHolder.tryGet(component: DataComponentType<T>): Option<T> = Option.of(this.get(component))
