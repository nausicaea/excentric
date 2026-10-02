package net.nausicaea.excentric.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BoundingBoxUtils;

import java.util.UUID;

public record Village(UUID id, BlockPos anchor, BlockPos center, BoundingBox boundingBox) {
	public final static Codec<Village> CODEC = RecordCodecBuilder
	    .create(i -> i.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Village::id),
	        BlockPos.CODEC.fieldOf("anchor").forGetter(Village::anchor),
	        BlockPos.CODEC.fieldOf("center").forGetter(Village::center),
	        BoundingBox.CODEC.fieldOf("boundingBox").forGetter(Village::boundingBox)).apply(i, Village::new));
	public final static StreamCodec<ByteBuf, Village> STREAM_CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC,
	    Village::id, BlockPos.STREAM_CODEC, Village::anchor, BlockPos.STREAM_CODEC, Village::center,
	    BoundingBoxUtils.STREAM_CODEC, Village::boundingBox, Village::new);
}
