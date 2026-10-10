package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.minecraft.core.Registry;
import net.nausicaea.excentric.ExcentricCommon;
import net.nausicaea.excentric.ai.need.Need;
import net.nausicaea.excentric.ai.need.Satiation;
import net.nausicaea.excentric.minecraft.core.registries.RegistriesKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Needs {
    private static final Logger LOG = LoggerFactory.getLogger(Needs.class);

    private Needs() {}

    public static final Need SATIATION =
            Registry.register(
                    RegistriesKt.NEED_REGISTRY, ExcentricCommon.id("satiation"), new Satiation());

    public static void register() {
        LOG.info(ExcentricCommon.MARKER, "Registering needs");
        RegistriesKt.NEED_REGISTRY.freeze();
    }
}
