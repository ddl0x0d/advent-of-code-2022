package aoc2022

import aoc2022.Day14.Terrain.*
import aoc2022.Direction.*
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sign

/**
 * [Day 14: Regolith Reservoir](https://adventofcode.com/2022/day/14)
 */
object Day14 : Puzzle<List<List<Point>>, Int> {

    override val name = "⌛⌛⌛ Regolith Reservoir"

    private val sandSource = Point(500, 0)
    private val sandDirections = listOf(UP.point, LEFT.point + UP.point, RIGHT.point + UP.point)

    override fun parseInput(lines: List<String>): List<List<Point>> =
        lines.map { line ->
            line.split(" -> ").map { coordinates ->
                coordinates.split(",").map { it.toInt() }.let { (x, y) -> Point(x, y) }
            }
        }

    /**
     * How many units of sand come to rest before sand starts flowing into the abyss below?
     */
    override fun part1(input: List<List<Point>>): Int {
        val rows = input.flatten().maxOf { it.y } + 1
        val cols = input.flatten().maxOf { it.x } + 1
        val grid = grid(rows, cols, input)
        return pourSandUntil(grid) { it.y + 1 == rows }
    }

    /**
     * How many units of sand come to rest until a unit of sand comes to rest at 500,0?
     */
    override fun part2(input: List<List<Point>>): Int {
        val rows = input.flatten().maxOf { it.y } + 3
        val cols = rows + sandSource.x
        val floor = listOf(Point(0, rows - 1), Point(cols - 1, rows - 1))
        val grid = grid(rows, cols, input + listOf(floor))
        return pourSandUntil(grid) { grid[sandSource].value != AIR }
    }

    private fun grid(rows: Int, cols: Int, paths: List<List<Point>>): Grid<Terrain> =
        Grid.from(rows, cols) { AIR }.also { grid ->
            paths.forEach { path ->
                path.windowed(2).forEach { (from, to) ->
                    val vector = to - from
                    val direction = vector.map { it.sign }
                    val length = max(vector.x.absoluteValue, vector.y.absoluteValue)
                    for (i in 0..length) {
                        grid[from + direction * i] = ROCK
                    }
                }
            }
        }

    private fun pourSandUntil(grid: Grid<Terrain>, stop: (Point) -> Boolean): Int {
        var atRest = 0
        while (true) {
            var sand = sandSource
            var next: Point? = sand
            while (next != null) {
                sand = next
                next = sandDirections
                    .map { sand + it }
                    .filter { it in grid }
                    .firstOrNull { grid[it].value == AIR }
            }
            if (stop(sand)) {
                break
            }
            grid[sand].value = SAND
            atRest++
        }
        print(grid)
        return atRest
    }

    private fun print(grid: Grid<Terrain>) {
        val rows = 0 until grid.rows
        val cols = (grid.cols - grid.rows * 2) until grid.cols
        grid.slice(rows, cols).print { it.value.char }
        println()
    }

    enum class Terrain(val char: Char) {
        ROCK('#'), AIR('.'), SAND('o')
    }
}
