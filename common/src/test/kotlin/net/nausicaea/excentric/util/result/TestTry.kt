package net.nausicaea.excentric.util.result

import net.nausicaea.excentric.util.option.None
import net.nausicaea.excentric.util.option.Some
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestTry {
    @Test
    fun isOkReturnsTrueForOk() {
        assertTrue(Ok(Unit).isOk())
    }

    @Test
    fun isOkReturnsFalseForErr() {
        assertFalse(Err(RuntimeException()).isOk())
    }

    @Test
    fun isErrReturnsFalseForOk() {
        assertFalse(Ok(Unit).isErr())
    }

    @Test
    fun isErrReturnsTrueForErr() {
        assertTrue(Err(RuntimeException()).isErr())
    }

    @Test
    fun okReturnsSomeForOk() {
        assertEquals(Ok(Unit).ok(), Some(Unit))
    }

    @Test
    fun okReturnsNoneForErr() {
        assertEquals(Err(RuntimeException()).ok(), None)
    }

    @Test
    fun inspectEagerlyCallsFunctionOnOk() {
        var invoked = false
        Ok(Unit).inspect { invoked = true }
        assertTrue(invoked)
    }

    @Test
    fun inspectDoesNotCallFunctionOnErr() {
        var invoked = false
        Err(RuntimeException()).inspect { invoked = true }
        assertFalse(invoked)
    }

    @Test
    fun inspectErrDoesNotCallFunctionOnOk() {
        var invoked = false
        Ok(Unit).inspectErr { invoked = true }
        assertFalse(invoked)
    }

    @Test
    fun inspectErrEagerlyCallsFunctionOnErr() {
        var invoked = false
        Err(RuntimeException()).inspectErr { invoked = true }
        assertTrue(invoked)
    }

    @Test
    fun mapTransformsWrappedValueOnOk() {
        assertEquals(Ok(Unit).map { true }, Ok(true))
        assertEquals(Ok(1).map { it + 1 }, Ok(2))
    }

    @Test
    fun mapDoesNotTransformOnErr() {
        var invoked = false
        Err(RuntimeException()).map {
            invoked = true
            true
        }
        assertFalse(invoked)
    }

    @Test
    fun flattenMergesNestedOks() {
        assertEquals(Ok(Ok(Unit)).flatten(), Ok(Unit))
    }

    @Test
    fun flattenKeepsOuterErr() {
        val r: Try<Try<Unit>> = Err(RuntimeException())
        assertTrue(r.flatten().isErr())
    }

    @Test
    fun flattenSurfacesInnerErr() {
        val r: Try<Try<Unit>> = Ok(Err(RuntimeException()))
        assertTrue(r.flatten().isErr())
    }
}
