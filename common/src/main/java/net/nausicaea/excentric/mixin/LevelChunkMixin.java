package net.nausicaea.excentric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.nausicaea.excentric.Todo;
import net.nausicaea.excentric.VillageManager;
import net.nausicaea.excentric.VillageRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin implements VillageRef {
	@Inject(method = "runPostLoad()V", at = @At("TAIL"))
	public void villageMod$runPostLoad(CallbackInfo ci) {
		var chunk = (LevelChunk) (Object) this;
		var level = chunk.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		VillageManager.get(serverLevel.getServer()).onChunkLoad(serverLevel, chunk);
	}

	@Override
	public Optional<UUID> villageMod$getVillageId() {
		throw new Todo();
	}

	@Override
	public void villageMod$setVillageId(UUID id) {
		throw new Todo();
	}
}
