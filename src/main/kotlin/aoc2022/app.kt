package aoc2022

import kotlin.io.path.Path
import kotlin.io.path.div

const val EXAMPLE_DIRECTORY = "examples"
const val EXAMPLE_FORMAT = "day-%02d.txt"

val puzzles = listOf(
    Day1, Day2, Day3, Day4, Day5,
    Day6, Day7, Day8, Day9, Day10,
    Day11, Day12, Day13, Day14, Day15(10, 20),
    Day16, Day17, Day18, Day19, Day20,
    Day21, Day22(cube1), Day23, Day24, Day25,
)

fun main() {
    println("🎄 Advent of Code 2022 🎄\n")
    puzzles.forEachIndexed { index, puzzle ->
        val day = index + 1
        println("--- Day $day: ${puzzle.name} ---")
        val path = Path(EXAMPLE_DIRECTORY) / EXAMPLE_FORMAT.format(day)
        val (part1, part2) = puzzle.solve(path)
        println("Part 1 answer = $part1")
        println("Part 2 answer = $part2")
        println()
    }
}
