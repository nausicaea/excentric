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

	public static BoundingBox extents(BlockPos anchor, int chunkRadius, int sectionHeight) {
		var halfWidth = 8 * chunkRadius;
		var halfHeight = 8 * sectionHeight;
		return new BoundingBox(anchor.getX() - halfWidth, anchor.getY() - halfHeight, anchor.getZ() - halfWidth,
		    anchor.getX() + halfWidth, anchor.getY() + halfHeight, anchor.getZ() + halfWidth);
	}
}
