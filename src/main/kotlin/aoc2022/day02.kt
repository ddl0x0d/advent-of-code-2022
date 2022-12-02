package aoc2022

import aoc2022.Day2.Outcome.*
import aoc2022.Day2.Shape.*

/**
 * [Day 2: Rock Paper Scissors](https://adventofcode.com/2022/day/2)
 */
object Day2 : Puzzle<List<Pair<Day2.Opponent, Day2.You>>, Int> {

    override val name = "✊🤚✌ Rock Paper Scissors"

    private val wins = mapOf(
        ROCK to SCISSORS,
        SCISSORS to PAPER,
        PAPER to ROCK,
    )

    private val loses = mapOf(
        ROCK to PAPER,
        SCISSORS to ROCK,
        PAPER to SCISSORS,
    )

    /**
     * List of rounds
     */
    override fun parseInput(lines: List<String>): List<Pair<Opponent, You>> =
        lines.map { round ->
            val (abc, _, xyz) = round.toCharArray()
            val opponent = Opponent.values().first { it.letter == abc }
            val you = You.values().first { it.letter == xyz }
            opponent to you
        }

    /**
     * What would your total score be if everything goes exactly according to your strategy guide?
     */
    override fun part1(input: List<Pair<Opponent, You>>): Int =
        input.sumOf { (opponent, your) ->
            val outcome = when (opponent.shape) {
                your.shape -> DRAW
                wins[your.shape] -> WIN
                loses[your.shape] -> LOSE
                else -> error("Unknown outcome")
            }
            outcome.score + your.shape.score
        }

    /**
     * What would your total score be if everything goes exactly according to your strategy guide?
     */
    override fun part2(input: List<Pair<Opponent, You>>): Int =
        input.sumOf { (opponent, your) ->
            val shape = when (your.outcome) {
                LOSE -> wins.getValue(opponent.shape)
                DRAW -> opponent.shape
                WIN -> loses.getValue(opponent.shape)
            }
            your.outcome.score + shape.score
        }

    enum class Shape(val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3),
    }

    enum class Opponent(val letter: Char, val shape: Shape) {
        A('A', ROCK),
        B('B', PAPER),
        C('C', SCISSORS),
    }

    enum class You(val letter: Char, val shape: Shape, val outcome: Outcome) {
        X('X', ROCK, LOSE),
        Y('Y', PAPER, DRAW),
        Z('Z', SCISSORS, WIN),
    }

    enum class Outcome(val score: Int) {
        LOSE(0),
        DRAW(3),
        WIN(6),
    }
}
