package net.nausicaea.excentric.mixin;

import net.minecraft.world.level.chunk.LevelChunk;
import net.nausicaea.excentric.Todo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
abstract class LevelChunkMixin {
	/// 1. Find the structure start chunk with
	///    [net.minecraft.world.level.StructureManager#getStructureWithPieceAt]
	/// 2. Query [net.nausicaea.excentric.VillageManager#findOrCreate] for the
	///    closest [net.nausicaea.excentric.Village] in range or trigger creation of
	///    one.
	/// 3. Find [net.minecraft.world.entity.npc.Villager]s and
	///    [net.minecraft.world.level.block.entity.BellBlockEntity], and link the new
	///    [net.nausicaea.excentric.Village#id()].
	@Inject(method = "runPostLoad()V", at = @At("TAIL"))
	public void villageMod$runPostLoad(CallbackInfo ci) {
		throw new Todo();
	}
}
