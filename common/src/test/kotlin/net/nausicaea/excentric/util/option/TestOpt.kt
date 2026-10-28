package net.nausicaea.excentric.util.option

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestOpt {
    @Test
    fun `Some is equivalent to Opt with value`() {
        assertEquals(Opt(100), Some(100))
    }

    @Test
    fun `None is equivalent to Opt with null`() {
        assertEquals(Opt(null), None)
    }

    @Test
    fun `isSome is true for Some`() {
        assertTrue(Some(100).isSome)
    }

    @Test
    fun `isSome is false for None`() {
        assertFalse(None.isSome)
    }

    @Test
    fun `isNone is false for Some`() {
        assertFalse(Some(100).isNone)
    }

    @Test
    fun `isNone is true for None`() {
        assertTrue(None.isNone)
    }

    @Test
    fun `map transforms value for Some`() {
        assertEquals(Some(101), Some(100).map { it + 1 })
    }

    @Test
    fun `map propagates None skipping transformation`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            None,
            opt.map {
                invoked = true
                it + 1
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `andThen transforms value for Some`() {
        assertEquals(Some(101), Some(100).andThen { Some(it + 1) })
    }

    @Test
    fun `andThen returns None when transformation returns None`() {
        assertEquals(None, Some(100).andThen { None })
    }

    @Test
    fun `andThen propagates None skipping transformation`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            None,
            opt.andThen {
                invoked = true
                Some(it + 1)
            },
        )
        assertEquals(
            None,
            opt.andThen {
                invoked = true
                None
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `inspect calls closure for Some and returns original value`() {
        var invoked = false
        assertEquals(
            Some(100),
            Some(100).inspect {
                invoked = true
                it + 1
            },
        )
        assertTrue(invoked)
    }

    @Test
    fun `inspect propagates None skipping the closure`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            None,
            opt.inspect {
                invoked = true
                it + 1
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `filter propagates None without calling predicate`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            None,
            opt.filter {
                invoked = true
                it > 1
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `filter invokes predicate on Some and returns Some if predicate returns true`() {
        var invoked = false
        assertEquals(
            Some(100),
            Some(100).filter {
                invoked = true
                it > 1
            },
        )
        assertTrue(invoked)
    }

    @Test
    fun `filter invokes predicate on Some and returns None if predicate returns false`() {
        var invoked = false
        assertEquals(
            None,
            Some(100).filter {
                invoked = true
                it < 1
            },
        )
        assertTrue(invoked)
    }

    @Test
    fun `forEach runs closure on Some`() {
        var invoked = false
        assertEquals(Unit, Some(100).forEach { invoked = true })
        assertTrue(invoked)
    }

    @Test
    fun `forEach skips closure on None`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(Unit, opt.forEach { invoked = true })
        assertFalse(invoked)
    }

    @Test
    fun `or returns the option if it contains a value`() {
        assertEquals(Some(100), Some(100).or(Some(200)))
    }

    @Test
    fun `or returns the other option if the first is None`() {
        val opt: Opt<Int> = None
        assertEquals(Some(200), None.or(Some(200)))
    }

    @Test
    fun `or returns None if both options are None`() {
        val opt: Opt<Int> = None
        assertEquals(None, opt.or(None))
    }

    @Test
    fun `orElse returns the option if it contains a value and skips the closure`() {
        var invoked = false
        assertEquals(
            Some(100),
            Some(100).orElse {
                invoked = true
                Some(200)
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `orElse returns the other option if the first is None`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            Some(200),
            opt.orElse {
                invoked = true
                Some(200)
            },
        )
        assertTrue(invoked)
    }

    @Test
    fun `orElse returns None if both options are None`() {
        var invoked = false
        val opt: Opt<Int> = None
        assertEquals(
            None,
            opt.orElse {
                invoked = true
                None
            },
        )
        assertTrue(invoked)
    }
}
