package net.nausicaea.excentric.ai

import net.nausicaea.excentric.ai.reasoner.ReasonerContext
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.utility.UtilityFnContext

interface NeedsContext :
    ReasonerContext<Service>,
    UtilityFnContext {
    fun needs(): NeedsCollection
}
