package net.nausicaea.excentric.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.UUID;

public record Village(UUID id, BlockPos anchor, BlockPos center, BoundingBox boundingBox) {
	public final static Codec<Village> CODEC = RecordCodecBuilder
	    .create(i -> i.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Village::id),
	        BlockPos.CODEC.fieldOf("anchor").forGetter(Village::anchor),
	        BlockPos.CODEC.fieldOf("center").forGetter(Village::center),
	        BoundingBox.CODEC.fieldOf("boundingBox").forGetter(Village::boundingBox)).apply(i, Village::new));
}
