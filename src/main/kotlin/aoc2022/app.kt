package aoc2022

import kotlin.io.path.Path
import kotlin.io.path.div

const val EXAMPLE_DIRECTORY = "examples"
const val EXAMPLE_FORMAT = "day-%02d.txt"

val puzzles = listOf(Day1, Day2)

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
