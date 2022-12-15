package aoc2022

import kotlin.math.max
import kotlin.math.min

operator fun IntRange.contains(other: IntRange): Boolean =
    first >= other.first && last <= other.last

infix fun IntRange.intersects(other: IntRange): Boolean =
    last >= other.first && first <= other.last

infix fun IntRange.union(other: IntRange): IntRange =
    min(first, other.first)..max(last, other.last)
