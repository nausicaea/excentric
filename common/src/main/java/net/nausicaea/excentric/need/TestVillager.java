package net.nausicaea.excentric.need;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/// Just a test entity
public final class TestVillager extends AbstractVillager {
	private final NeedsReasoner reasoner;
	private final EveryN everyNTicks;
	private final RandomSource rng;

	public TestVillager(EntityType<? extends AbstractVillager> entityType, Level level) {
		super(entityType, level);
		reasoner = new NeedsReasoner(new Needs());
		everyNTicks = new EveryN(32, level.random.nextInt(32));
		rng = level.random.fork();
	}

	/// Deliberately don't call `super.tick()` (i.e.
	/// [net.minecraft.world.entity.Mob#tick()], ).
	@Override
	public void tick() {
		reasoner.tryRealiseService();
		everyNTicks.run(() -> reasoner.plan(new NeedsContext(), rng));
	}

	@Override
	protected void rewardTradeXp(MerchantOffer merchantOffer) {
		// Don't do anything.
	}

	@Override
	protected void updateTrades() {
		// Don't do anything.
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
		// Don't do anything.
		return null;
	}
}
