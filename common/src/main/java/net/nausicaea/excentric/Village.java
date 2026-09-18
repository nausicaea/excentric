package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public final class Village {
	public record Data(UUID id, GlobalPos anchor) {
	}

	public final static Codec<Data> CODEC = RecordCodecBuilder
	    .create(i -> i.group(UUIDUtil.CODEC.fieldOf("id").forGetter(Data::id),
	        GlobalPos.CODEC.fieldOf("anchor").forGetter(Data::anchor)).apply(i, Data::new));
	private final UUID id;
	private final Runnable setDirty;
	private GlobalPos anchor;

	Village(UUID id, Runnable setDirty, GlobalPos anchor) {
		this.id = id;
		this.setDirty = setDirty;
		this.anchor = anchor;
	}

	public UUID id() {
		return id;
	}

	public void setAnchor(GlobalPos anchor) {
		this.anchor = anchor;
		setDirty.run();
	}

	Data export() {
		return new Data(id, anchor);
	}
}
