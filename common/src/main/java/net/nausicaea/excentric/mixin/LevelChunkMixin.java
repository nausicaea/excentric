package net.nausicaea.excentric.mixin;

import net.minecraft.world.level.chunk.LevelChunk;
import net.nausicaea.excentric.Todo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
	@Inject(method = "runPostLoad()V", at = @At("TAIL"))
	public void villageMod$runPostLoad(CallbackInfo ci) {
		throw new Todo();
	}
}
