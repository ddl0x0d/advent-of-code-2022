package aoc2022

/**
 * [Day 1: Sonar Sweep](https://adventofcode.com/2021/day/1)
 */
object Day1 : Puzzle<List<Int>, Int> {

    override val name = "📡 Sonar Sweep"

    override fun parseInput(lines: List<String>): List<Int> = lines.map { it.toInt() }

    /**
     * How many measurements are larger than the previous measurement?
     */
    override fun part1(input: List<Int>): Int = input.windowed(2).count { (a, b) -> a < b }

    /**
     * How many sums are larger than the previous sum?
     */
    override fun part2(input: List<Int>): Int = input.windowed(4).count { (a, _, _, b) -> a < b }
}
