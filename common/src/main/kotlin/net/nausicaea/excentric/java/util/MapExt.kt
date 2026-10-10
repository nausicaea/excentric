package net.nausicaea.excentric.java.util

import net.nausicaea.excentric.util.Option

fun <K, V> Map<K, V>.tryGet(key: K): Option<V> = Option.of(this.get(key))
