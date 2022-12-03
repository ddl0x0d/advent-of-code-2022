package aoc2022

/**
 * [Day 3: Rucksack Reorganization](https://adventofcode.com/2022/day/3)
 */
object Day3 : Puzzle<List<String>, Int> {

    override val name = "🎒🧭🌴 Rucksack Reorganization"

    /**
     * What is the sum of the priorities of item types that appears in both compartments?
     */
    override fun part1(input: List<String>): Int =
        input.sumOf { rucksack ->
            val half = rucksack.length / 2
            val items1 = rucksack.take(half).items
            val items2 = rucksack.drop(half).items
            val intersection = items1 intersect items2
            intersection.first().priority
        }

    /**
     * What is the sum of the priorities of item types common between each group of three Elves?
     */
    override fun part2(input: List<String>): Int =
        input.chunked(3).sumOf { group ->
            group.map { it.items }
                .reduce { common, items -> common intersect items }
                .first().priority
        }

    private val String.items: Set<Char>
        get() = toCharArray().toSet()

    private val Char.priority: Int
        get() = code - (if (isLowerCase()) 'a'.code else 'A'.code - 26) + 1
}
