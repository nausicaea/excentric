package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record Village(UUID id, GlobalPos anchor, GlobalPos center, int radius, int height) {
	public final static Codec<Village> CODEC = RecordCodecBuilder
	    .create(i -> i.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Village::id),
	        GlobalPos.CODEC.fieldOf("anchor").forGetter(Village::anchor),
	        GlobalPos.CODEC.fieldOf("center").forGetter(Village::center),
	        Codec.INT.fieldOf("radius").forGetter(Village::radius),
	        Codec.INT.fieldOf("height").forGetter(Village::height)).apply(i, Village::new));
}
