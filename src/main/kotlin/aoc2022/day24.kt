package aoc2022

import aoc2022.Day24.Terrain.GROUND
import aoc2022.Day24.Terrain.WALL
import aoc2022.Direction.*
import aoc2022.Point.Companion.ONE
import aoc2022.Point.Companion.ZERO

/**
 * [Day 24: Blizzard Basin](https://adventofcode.com/2022/day/24)
 */
object Day24 : Puzzle<List<String>, Int> {

    override val name = "🏔❄🏔 Blizzard Basin"

    /**
     * What is the fewest number of minutes required to avoid the blizzards and reach the goal?
     */
    override fun part1(input: List<String>): Int =
        Valley(input).run { timeToReachGoal(entrance, exit) }

    /**
     * What is the fewest number of minutes required to reach the goal, go back to the start, then reach the goal again?
     */
    override fun part2(input: List<String>): Int =
        Valley(input).run {
            listOf(
                timeToReachGoal(entrance, exit),
                timeToReachGoal(exit, entrance),
                timeToReachGoal(entrance, exit),
            ).reduce(Int::plus)
        }

    class Valley(input: List<String>) {

        private val grid = Grid.read(input) { char, _ -> Terrain.values().first { it.char == char } }
        private val blizzards = grid.filter { it.value.isBlizzard() }.map { Blizzard(it.point, it.value) }

        val entrance = Point(1, 0)
        val exit = grid.size - ONE - entrance

        fun timeToReachGoal(start: Point, goal: Point): Int =
            generateSequence(setOf(start)) { expeditions ->
                move(blizzards)
                move(expeditions)
            }.indexOfFirst { goal in it }

        private fun move(blizzards: List<Blizzard>) {
            blizzards.forEach { grid[it.point] = GROUND }
            blizzards.forEach { it.move() }
            blizzards.forEach { grid[it.point] = it.terrain }
        }

        private fun Blizzard.move() {
            point += terrain.vector
            if (grid[point].value == WALL) {
                point -= terrain.vector * (grid.size - ONE * 2)
            }
        }

        private fun move(expeditions: Set<Point>): Set<Point> =
            expeditions.flatMap { expedition ->
                (grid.neighbours(expedition) + grid[expedition])
                    .filter { it.value == GROUND }
                    .map { it.point }
            }.toSet()

        private data class Blizzard(var point: Point, val terrain: Terrain)
    }

    enum class Terrain(val char: Char, val vector: Point = ZERO) {
        WALL('#'),
        GROUND('.'),
        BLIZZARD_UP('^', UP.gridPoint),
        BLIZZARD_DOWN('v', DOWN.gridPoint),
        BLIZZARD_LEFT('<', LEFT.gridPoint),
        BLIZZARD_RIGHT('>', RIGHT.gridPoint);

        fun isBlizzard() = name.startsWith("BLIZZARD")
    }
}
