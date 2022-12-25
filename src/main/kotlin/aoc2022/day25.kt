package aoc2022

/**
 * [Day 25: Full of Hot Air](https://adventofcode.com/2022/day/25)
 */
object Day25 : Puzzle<List<String>, String> {

    override val name = "🎈🎈🎈 Full of Hot Air"

    /**
     * What SNAFU number do you supply to Bob's console?
     */
    override fun part1(input: List<String>): String =
        input.sumOf { it.snafuToDecimal() }.decimalToSnafu()

    private fun String.snafuToDecimal(): Long =
        reversed().fold(0L to 1L) { (result, power), char ->
            result + when (char) {
                '0', '1', '2' -> char.digitToInt() * power
                '-' -> -power
                else -> -power * 2
            } to power * 5
        }.first

    private fun Long.decimalToSnafu(): String =
        generateSequence("" to this) { (result, number) ->
            val (char, adjustedNumber) = when (val remainder = number % 5) {
                in 0..2 -> "$remainder" to number
                3L -> "=" to number + 2
                else -> "-" to number + 1
            }
            char + result to adjustedNumber / 5
        }.first { it.second == 0L }.first

    override fun part2(input: List<String>) = "AoC 2022 completed!"
}
