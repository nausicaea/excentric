package net.nausicaea.excentric.minecraft.world.level.levelgen.structure.structures

import net.minecraft.core.Holder
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure
import net.nausicaea.excentric.mixin.accessor.JigsawStructureAccessor

/**
 * Helper property to hide the ugly double cast for accessing the jigsaw structure's start pool.
 *
 * SAFETY: Final classes (JigsawStructure) must have the intermediate cast to
 * Object (see
 * https://docs.fabricmc.net/develop/mixins/accessors#accessors-for-final-classes).
 */
@Suppress("KotlinConstantConditions")
val JigsawStructure.startPool: Holder<StructureTemplatePool>
    get() = (this as Any as JigsawStructureAccessor).`villageMod$getStartPool`()
