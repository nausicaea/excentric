package net.nausicaea.excentric.fabric.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

@JvmField
val AFTER_BLOCK_PLACE: Event<AfterBlockPlace> =
    EventFactory.createArrayBacked(
        AfterBlockPlace::class.java,
    ) { callbacks ->
        { prevState, pos, level ->
            for (callback in callbacks) {
                callback.afterBlockPlace(prevState, pos, level)
            }
        }
    }
