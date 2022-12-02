package aoc2022

/**
 * [Day 1: Calorie Counting](https://adventofcode.com/2022/day/1)
 */
object Day1 : Puzzle<List<Int>, Int> {

    override val name = "🍎🍐🍇 Calorie Counting"

    /**
     * List of calories per elf
     */
    override fun parseInput(lines: List<String>): List<Int> =
        lines.fold(ArrayDeque()) { elves, calories ->
            elves.apply {
                addLast(calories.toIntOrNull()?.let {
                    it + (removeLastOrNull() ?: 0)
                } ?: 0)
            }
        }

    /**
     * How many total Calories is that Elf carrying?
     */
    override fun part1(input: List<Int>): Int = input.max()

    /**
     * How many Calories are top three Elves carrying in total?
     */
    override fun part2(input: List<Int>): Int = input.sortedDescending().take(3).sum()
}
