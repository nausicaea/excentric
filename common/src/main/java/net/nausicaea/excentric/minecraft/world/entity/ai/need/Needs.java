package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.minecraft.core.Registry;
import net.nausicaea.excentric.ExcentricCommon;
import net.nausicaea.excentric.ExcentricRegistries;
import net.nausicaea.excentric.minecraft.world.entity.ai.Need;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Needs {
	private static final Logger LOG = LoggerFactory.getLogger(Needs.class);

	private Needs() {
	}

	public static final Need SATIATION = Registry.register(ExcentricRegistries.NEEDS, ExcentricCommon.id("satiation"),
	    new Satiation());

	public static void register() {
		LOG.info(ExcentricCommon.MARKER, "Registering needs");
		ExcentricRegistries.NEEDS.freeze();
	}
}
