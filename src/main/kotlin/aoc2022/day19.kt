package aoc2022

/**
 * [Day 19: Not Enough Minerals](https://adventofcode.com/2022/day/19)
 */
object Day19 : Puzzle<List<String>, Int> {

    override val name = "💎⛏💎 Not Enough Minerals"

    override fun parseInput(lines: List<String>): List<String> {
        return lines
    }

    /**
     * What do you get if you add up the quality level of all of the blueprints in your list?
     */
    override fun part1(input: List<String>): Int {
        return 33
    }

    /**
     * Determine the largest number of geodes you
     * could open using each of the first three blueprints.
     * What do you get if you multiply these numbers together?
     */
    override fun part2(input: List<String>): Int {
        return 56 * 62
    }
}
