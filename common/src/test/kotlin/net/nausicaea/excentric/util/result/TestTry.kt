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
        assertEquals(Some(Unit), Ok(Unit).ok())
    }

    @Test
    fun okReturnsNoneForErr() {
        assertEquals(None, Err(RuntimeException()).ok())
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
        assertEquals(Ok(true), Ok(Unit).map { true })
        assertEquals(Ok(2), Ok(1).map { it + 1 })
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
        assertEquals(Ok(Unit), Ok(Ok(Unit)).flatten())
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
