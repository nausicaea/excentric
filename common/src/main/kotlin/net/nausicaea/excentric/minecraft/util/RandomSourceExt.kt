package net.nausicaea.excentric.minecraft.util

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.ai.reasoner.ReasonerRng

fun RandomSource.asReasonerRng(): ReasonerRng = { this.nextDouble() }
