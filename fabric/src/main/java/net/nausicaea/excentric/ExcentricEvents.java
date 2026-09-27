package net.nausicaea.excentric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class ExcentricEvents {
	public static final Event<AfterBlockPlace> AFTER_BLOCK_PLACE = EventFactory.createArrayBacked(AfterBlockPlace.class,
	    callbacks -> (prevState, pos, level) -> {
		    for (AfterBlockPlace callback : callbacks) {
			    callback.afterBlockPlace(prevState, pos, level);
		    }
	    });

	@FunctionalInterface
	public interface AfterBlockPlace {
		void afterBlockPlace(BlockState prevState, BlockPos pos, ServerLevel level);
	}

	private ExcentricEvents() {
	}
}
