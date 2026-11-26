package net.nausicaea.excentric.ai.service

import net.nausicaea.excentric.ai.NeedsCollection

data class BlockServiceContext(
    val subject: Sleeper,
    val needs: NeedsCollection,
)
