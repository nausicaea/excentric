package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.UUID;

public record Village(UUID id, GlobalPos anchor, BlockPos center, BoundingBox boundingBox) {
	public final static Codec<Village> CODEC = RecordCodecBuilder
	    .create(i -> i.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Village::id),
	        GlobalPos.CODEC.fieldOf("anchor").forGetter(Village::anchor),
	        BlockPos.CODEC.fieldOf("center").forGetter(Village::center),
	        BoundingBox.CODEC.fieldOf("boundingBox").forGetter(Village::boundingBox)).apply(i, Village::new));
}
