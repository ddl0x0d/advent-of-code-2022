package aoc2022

/**
 * [Day 4: Camp Cleanup](https://adventofcode.com/2022/day/4)
 */
object Day4 : Puzzle<List<Pair<IntRange, IntRange>>, Int> {

    override val name = "🏕🧹🧽 Camp Cleanup"

    private val regex = "(\\d+)-(\\d+),(\\d+)-(\\d+)".toRegex()

    /**
     * List of the section assignments for each pair
     */
    override fun parseInput(lines: List<String>): List<Pair<IntRange, IntRange>> = lines.map { line ->
        val (a, b, c, d) = regex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
        a..b to c..d
    }

    /**
     * In how many assignment pairs does one range fully contain the other?
     */
    override fun part1(input: List<Pair<IntRange, IntRange>>): Int =
        input.count { (elf1, elf2) -> elf1 in elf2 || elf2 in elf1 }

    /**
     * In how many assignment pairs do the ranges overlap?
     */
    override fun part2(input: List<Pair<IntRange, IntRange>>): Int =
        input.count { (elf1, elf2) -> elf1 intersects elf2 }
}
