package aoc2022

import aoc2022.Point.Companion.ZERO
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * [Day 9: Rope Bridge](https://adventofcode.com/2022/day/9)
 */
object Day9 : Puzzle<List<Pair<Direction, Int>>, Int> {

    override val name = "🐍🐍🐍 Rope Bridge"

    /**
     * Series of motions
     */
    override fun parseInput(lines: List<String>): List<Pair<Direction, Int>> =
        lines.map { line ->
            val (direction, steps) = line.split(" ")
            Direction.values().first { it.name.startsWith(direction) } to steps.toInt()
        }

    /**
     * How many positions does the tail of the rope visit at least once?
     */
    override fun part1(input: List<Pair<Direction, Int>>): Int = positionsVisitedByRopeTail(input, 1)

    /**
     * How many positions does the tail of the rope visit at least once?
     */
    override fun part2(input: List<Pair<Direction, Int>>): Int = positionsVisitedByRopeTail(input, 9)

    private fun positionsVisitedByRopeTail(input: List<Pair<Direction, Int>>, tailSize: Int): Int {
        var head = ZERO
        var tail = List(tailSize) { head }
        return input.fold(setOf(head)) { visited, (direction, steps) ->
            (1..steps).fold(visited) { innerVisited, _ ->
                head += direction.point
                var lastKnot = head
                tail = tail.map { knot ->
                    val vector = lastKnot - knot
                    val touching = vector.map { it.absoluteValue }.run { x <= 1 && y <= 1 }
                    val step = if (touching) ZERO else vector.map { it.sign }
                    (knot + step).also { lastKnot = it }
                }
                innerVisited + lastKnot
            }
        }.size
    }
}
