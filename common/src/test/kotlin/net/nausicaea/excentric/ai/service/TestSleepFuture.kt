package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.nausicaea.excentric.ai.NeedsCollection
import net.nausicaea.excentric.ai.need.MissingNeed
import net.nausicaea.excentric.assertPending
import net.nausicaea.excentric.assertReadyErr
import net.nausicaea.excentric.assertReadyOk
import net.nausicaea.excentric.minecraft.core.RESTEDNESS
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `first poll bails when subject is already sleeping`() {
        val subject = FakeSleeper(true)
        val needs = NeedsCollection()
        needs.add(RESTEDNESS, Probability.ZERO)
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertReadyErr<AlreadyAsleep, _>(fut.poll())
        assertTrue(subject.unchanged)
    }

    @Test
    fun `first poll bails when there is no restedness need`() {
        val subject = FakeSleeper(false)
        val needs = NeedsCollection()
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertReadyErr<MissingNeed, _>(fut.poll())
        assertTrue(subject.unchanged)
    }

    @Test
    fun `first poll ends successfully if the need is satisfied`() {
        val subject = FakeSleeper(false)
        val needs = NeedsCollection()
        needs.add(RESTEDNESS, Probability.ONE)
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertReadyOk(Probability.ONE, fut.poll())
        assertTrue(subject.unchanged)
    }

    @Test
    fun `first poll calls startSleeping with correct BlockPos`() {
        val subject = FakeSleeper(false)
        val needs = NeedsCollection()
        needs.add(RESTEDNESS, Probability.ZERO)
        val need = NEED_REGISTRY.getResourceKey(RESTEDNESS).get()
        val increment = Probability.of(0.1)

        val fut = SleepFuture(subject, needs, need, increment, BlockPos.ZERO)
        assertPending(fut.poll())
        assertEquals(1, subject.startInvocations)
        assertEquals(listOf(BlockPos.ZERO), subject.startSleepingArgs)
    }
}

private class FakeSleeper(
    override val isSleeping: Boolean,
) : Sleeper {
    var startInvocations: Int = 0
    var stopInvocations: Int = 0
    val startSleepingArgs: MutableList<BlockPos> = mutableListOf()

    val unchanged: Boolean
        get() = startInvocations == 0 && stopInvocations == 0

    override fun startSleeping(pos: BlockPos) {
        startInvocations += 1
        startSleepingArgs.add(pos)
    }

    override fun stopSleeping() {
        stopInvocations += 1
    }
}
