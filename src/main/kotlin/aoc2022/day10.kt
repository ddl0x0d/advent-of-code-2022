package aoc2022

import aoc2022.Day10.Instruction

/**
 * [Day 10: Cathode-Ray Tube](https://adventofcode.com/2022/day/10)
 */
object Day10 : Puzzle<List<Instruction>, Int> {

    override val name = "🖥🟡⚫ Cathode-Ray Tube"

    private const val NOOP = "noop"
    private const val ADD_X = "addx "

    override fun parseInput(lines: List<String>): List<Instruction> =
        lines.map { line ->
            if (line == NOOP) {
                Instruction(cycles = 1, increment = 0)
            } else {
                Instruction(cycles = 2, increment = line.substring(ADD_X.length).toInt())
            }
        }

    /**
     * What is the sum of these six signal strengths?
     * (during the 20th, 60th, 100th, 140th, 180th, and 220th cycles)
     */
    override fun part1(input: List<Instruction>): Int {
        val signalStrengths = mutableListOf<Int>()
        process(input) { cycle, x ->
            if (cycle % 40 == 20) {
                signalStrengths += cycle * x
            }
        }
        return signalStrengths.reduce(Int::plus)
    }

    private const val CRT_WIDTH = 40
    private const val CRT_HEIGHT = 6

    /**
     * What eight capital letters appear on your CRT?
     */
    override fun part2(input: List<Instruction>): Int {
        val crt = Array(CRT_HEIGHT) { Array(CRT_WIDTH) { ' ' } }
        process(input) { cycle, x ->
            val col = (cycle - 1) % CRT_WIDTH
            val row = (cycle - 1) / CRT_WIDTH
            crt[row][col] = if (col in (x - 1..x + 1)) '#' else '.'
        }
        print(crt)
        return 0
    }

    private fun process(input: List<Instruction>, process: (cycle: Int, x: Int) -> Unit) {
        input.flatMap {
            List(it.cycles - 1) { 0 } + it.increment
        }.foldIndexed(1) { i, x, increment ->
            val cycle = i + 1
            process(cycle, x)
            x + increment
        }
    }

    private fun print(crt: Array<Array<Char>>) {
        crt.forEach { row ->
            row.forEach { cell ->
                print(cell)
            }
            println()
        }
    }

    data class Instruction(val cycles: Int, val increment: Int)
}
