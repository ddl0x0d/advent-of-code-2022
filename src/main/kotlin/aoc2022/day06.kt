package aoc2022

/**
 * [Day 6: Tuning Trouble](https://adventofcode.com/2022/day/6)
 */
object Day6 : Puzzle<String, Int> {

    override val name = "📱0⃣1⃣ Tuning Trouble"

    /**
     * Data stream buffer
     */
    override fun parseInput(lines: List<String>): String = lines.first()

    /**
     * How many characters need to be processed before the first start-of-packet marker is detected?
     */
    override fun part1(input: String): Int = lockOnSignal(input, 4)

    /**
     * How many characters need to be processed before the first start-of-message marker is detected?
     */
    override fun part2(input: String): Int = lockOnSignal(input, 14)

    private fun lockOnSignal(input: String, distinctChars: Int): Int =
        input.windowedSequence(distinctChars) {
            it.toSet().size
        }.withIndex().first {
            it.value == distinctChars
        }.index + distinctChars
}
