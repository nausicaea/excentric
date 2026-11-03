package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.nausicaea.excentric.ai.NeedsCollection
import net.nausicaea.excentric.ai.need.MissingNeed
import net.nausicaea.excentric.assertReadyErr
import net.nausicaea.excentric.minecraft.core.RESTEDNESS
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestSleepFuture {
    @Test
    fun `constructor does not modify sleeper state`() {
        val subject = FakeSleeper(false)
        val needs = NeedsCollection()
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertFalse(subject.isSleeping)
        assertTrue(subject.unchanged)
    }

    @Test
    fun `first poll errors out when subject is already sleeping`() {
        val subject = FakeSleeper(true)
        val needs = NeedsCollection()
        needs.add(RESTEDNESS, Probability.ONE)
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertReadyErr<AlreadyAsleep, _>(fut.poll())
        assertTrue(subject.unchanged)
    }

    @Test
    fun `first poll errors out when there is no restedness need`() {
        val subject = FakeSleeper(false)
        val needs = NeedsCollection()
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertReadyErr<MissingNeed, _>(fut.poll())
        assertTrue(subject.unchanged)
    }
}

private class FakeSleeper(
    override val isSleeping: Boolean,
) : Sleeper {
    var startInvocations: Int = 0
    var stopInvocations: Int = 0

    val unchanged: Boolean
        get() = startInvocations == 0 && stopInvocations == 0

    override fun startSleeping(pos: BlockPos) {
        startInvocations += 1
    }

    override fun stopSleeping() {
        stopInvocations += 1
    }
}
