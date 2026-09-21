package net.nausicaea.excentric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class ExcentricEvents {
	public static final Event<AfterBlockPlace> AFTER_BLOCK_PLACE = EventFactory.createArrayBacked(AfterBlockPlace.class,
	    callbacks -> (prevState, level, pos, newState, movedByPiston) -> {
		    for (AfterBlockPlace callback : callbacks) {
			    callback.afterBlockPlace(prevState, level, pos, newState, movedByPiston);
		    }
	    });

	@FunctionalInterface
	public interface AfterBlockPlace {
		void afterBlockPlace(BlockState prevState, Level level, BlockPos pos, BlockState newState,
		    boolean movedByPiston);
	}

	private ExcentricEvents() {
	}
}
