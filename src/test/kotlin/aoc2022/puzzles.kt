package aoc2022

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.div

data class TestCase<T>(val day: Int, val puzzle: Puzzle<*, T>, val part1: T, val part2: T) : WithDataTestName {
    override fun dataTestName(): String = "Day $day: ${puzzle.name}"
}

class PuzzleTests : StringSpec({
    withData(
        listOf(
            TestCase(1, Day1, 24_000, 45_000),
            TestCase(2, Day2, 15, 12),
            TestCase(3, Day3, 157, 70),
            TestCase(4, Day4, 2, 4),
            TestCase(5, Day5, "CMZ", "MCD"),
            TestCase(6, Day6, 7, 19),
            TestCase(7, Day7, 95437, 24933642),
            TestCase(8, Day8, 21, 8),
            TestCase(9, Day9, 88, 36),
            TestCase(10, Day10, 13140, 0),
            TestCase(11, Day11, 10605, 2713310158),
        )
    ) { (day, puzzle, answer1, answer2) ->
        val path = Path(EXAMPLE_DIRECTORY) / EXAMPLE_FORMAT.format(day)
        val (part1, part2) = puzzle.solve(path)
        assertSoftly {
            withClue("Part 1 answer") { part1 shouldBe answer1 }
            withClue("Part 2 answer") { part2 shouldBe answer2 }
        }
    }
})
