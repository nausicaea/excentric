package net.nausicaea.excentric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.nausicaea.excentric.VillageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
	@Inject(method = "runPostLoad()V", at = @At("TAIL"))
	public void villageMod$runPostLoad(CallbackInfo ci) {
		var chunk = (LevelChunk) (Object) this;
		var level = chunk.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		VillageManager.get(serverLevel.getServer()).onChunkLoad(serverLevel, chunk);
	}
}
