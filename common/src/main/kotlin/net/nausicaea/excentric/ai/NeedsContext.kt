package net.nausicaea.excentric.ai

import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.utility.UtilityFnContext
import net.nausicaea.excentric.minecraft.world.entity.ai.NeedsCollection

interface NeedsContext :
    ReasonerContext<Service>,
    UtilityFnContext {
    fun needs(): NeedsCollection
}
