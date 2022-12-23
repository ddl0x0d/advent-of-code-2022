package aoc2022

import aoc2022.Direction.*
import aoc2022.Point.Companion.ONE

/**
 * [Day 23: Unstable Diffusion](https://adventofcode.com/2022/day/23)
 */
object Day23 : Puzzle<Grid<Boolean>, Int> {

    override val name = "🌱🧝🌱 Unstable Diffusion"

    override fun parseInput(lines: List<String>): Grid<Boolean> =
        Grid.read(lines) { char, _ -> char == '#' }

    /**
     * How many empty ground tiles does that rectangle contain?
     */
    override fun part1(input: Grid<Boolean>): Int = simulate(input, until = { it == 10 }).let { (_, elves) ->
        elves.smallestRectangle().let { (x, y) -> x * y } - elves.size
    }

    /**
     * What is the number of the first round where no Elf moves?
     */
    override fun part2(input: Grid<Boolean>): Int = simulate(input).let { (round, _) -> round }

    private fun simulate(input: Grid<Boolean>, until: (Int) -> Boolean = { false }): Pair<Int, Set<Point>> {
        var elves = input.filter { it.value }.map { it.point }.toSet()
        val directions = directions.toMutableMap()
        var round = 0
        do {
            round++
            val active = elves.filter { elf ->
                val xs = elf.x - 1..elf.x + 1
                val ys = elf.y - 1..elf.y + 1
                elves.any { it != elf && it.x in xs && it.y in ys }
            }
            if (active.isEmpty()) {
                break
            }
            val proposedDirections = active.associateWith { elf ->
                directions.entries.firstOrNull { (_, points) ->
                    points.none { (elf + it) in elves }
                }?.key
            }
            val proposedMoves = proposedDirections
                .filterValues { it != null }
                .mapValues { (elf, direction) ->
                    elf + direction!!.gridPoint
                }
            val moves = proposedMoves.toList().groupBy(
                keySelector = { it.second },
                valueTransform = { it.first }
            ).filterValues { it.size == 1 }
                .mapValues { it.value.first() }
                .toList()
                .associate { it.second to it.first }
            elves = elves.mapTo(mutableSetOf()) { elf ->
                moves[elf] ?: elf
            }

            val firstDirection = directions.keys.first()
            val firstPoints = directions.remove(firstDirection)!!
            directions[firstDirection] = firstPoints
        } while (!until(round))
        return round to elves
    }

    private val directions: Map<Direction, Set<Point>> = linkedMapOf(
        UP to setOf(UP.gridPoint, UP.gridPoint + RIGHT.gridPoint, UP.gridPoint + LEFT.gridPoint),
        DOWN to setOf(DOWN.gridPoint, DOWN.gridPoint + RIGHT.gridPoint, DOWN.gridPoint + LEFT.gridPoint),
        LEFT to setOf(LEFT.gridPoint, LEFT.gridPoint + UP.gridPoint, LEFT.gridPoint + DOWN.gridPoint),
        RIGHT to setOf(RIGHT.gridPoint, RIGHT.gridPoint + UP.gridPoint, RIGHT.gridPoint + DOWN.gridPoint),
    )

    private fun Collection<Point>.smallestRectangle(): Point {
        val max = Point(maxOf { it.x }, maxOf { it.y })
        val min = Point(minOf { it.x }, minOf { it.y })
        return max - min + ONE
    }
}
