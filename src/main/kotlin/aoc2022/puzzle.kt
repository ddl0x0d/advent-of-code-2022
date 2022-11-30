package aoc2022

import java.nio.file.Path
import kotlin.io.path.readLines

interface Puzzle<INPUT, OUTPUT> {

    val name: String

    fun solve(path: Path): Pair<OUTPUT, OUTPUT> {
        val input = parseInput(path.readLines())
        val part1 = part1(input)
        val part2 = part2(input)
        return part1 to part2
    }

    @Suppress("UNCHECKED_CAST")
    fun parseInput(lines: List<String>): INPUT = lines as INPUT

    fun part1(input: INPUT): OUTPUT
    fun part2(input: INPUT): OUTPUT
}
