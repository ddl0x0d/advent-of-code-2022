package aoc2022

import aoc2022.Day5.CargoCrane
import aoc2022.Day5.CargoCrane.Instruction

/**
 * [Day 5: Supply Stacks](https://adventofcode.com/2022/day/5)
 */
object Day5 : Puzzle<CargoCrane, String> {

    override val name = "🏗📦👷 Supply Stacks"

    private val instructionRegex = "move (\\d+) from (\\d+) to (\\d+)".toRegex()

    override fun parseInput(lines: List<String>): CargoCrane {
        val separatorIndex = lines.indexOfFirst { it.isEmpty() }
        val stackLines = lines.slice(0 until separatorIndex - 1)
        val stackIndices = lines[separatorIndex - 1].withIndex().filter { it.value.isDigit() }.map { it.index }
        val stacks = stackIndices.map { stackIndex ->
            stackLines.reversed().mapNotNull { it.getOrNull(stackIndex) }.filter { it.isLetter() }.joinToString("")
        }
        val instructionLines = lines.slice(separatorIndex + 1 until lines.size)
        val instructions = instructionLines.map { instructionLine ->
            val (amount, from, to) = instructionRegex.matchEntire(instructionLine)!!
                .groupValues.drop(1).map { it.toInt() }
            Instruction(amount, from - 1, to - 1)
        }
        return CargoCrane(stacks, instructions)
    }

    /**
     * After the rearrangement procedure completes, what crate ends up on top of each stack?
     */
    override fun part1(input: CargoCrane): String = input.rearrange { it.reversed() }.topCrates()

    /**
     * After the rearrangement procedure completes, what crate ends up on top of each stack?
     */
    override fun part2(input: CargoCrane): String = input.rearrange { it }.topCrates()

    private fun List<String>.topCrates() = joinToString("") { it.last().toString() }

    data class CargoCrane(
        val stacks: List<String>,
        val instructions: List<Instruction>,
    ) {
        fun rearrange(processCrates: (String) -> String): List<String> =
            instructions.fold(stacks.toMutableList()) { stacks, (amount, from, to) ->
                val crates = stacks[from].takeLast(amount)
                stacks[to] += processCrates(crates)
                stacks[from] = stacks[from].dropLast(amount)
                stacks
            }

        data class Instruction(val amount: Int, val from: Int, val to: Int)
    }
}
