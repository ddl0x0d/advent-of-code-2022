package aoc2022

import aoc2022.Day21.Operator.*
import aoc2022.Day21.OperatorMonkey

/**
 * [Day 21: Monkey Math](https://adventofcode.com/2022/day/21)
 */
object Day21 : Puzzle<OperatorMonkey, Long> {

    override val name = "🐒🧮🐒 Monkey Math"

    private const val ROOT = "root"
    private const val HUMAN = "humn"

    private val number = "\\d+".toRegex()

    override fun parseInput(lines: List<String>): OperatorMonkey =
        lines.associate { line ->
            val (name, job) = line.split(": ")
            val monkey = if (job matches number) {
                NumberMonkey(name, job.toLong())
            } else {
                val (name1, operator, name2) = job.split(" ")
                OperatorMonkey(
                    name = name, name1 = name1, name2 = name2,
                    operator = values().first { it.sign == operator }
                )
            }
            name to monkey
        }.also { monkeys ->
            monkeys.values.filterIsInstance<OperatorMonkey>().forEach { monkey ->
                monkey.monkey1 = monkeys.getValue(monkey.name1)
                monkey.monkey2 = monkeys.getValue(monkey.name2)
            }
        }.getValue(ROOT) as OperatorMonkey

    /**
     * What number will the monkey named `root` yell?
     */
    override fun part1(input: OperatorMonkey): Long = input.calculate()

    private fun Monkey.calculate(): Long =
        when (this) {
            is NumberMonkey -> number
            is OperatorMonkey -> run {
                val op1 = monkey1.calculate()
                val op2 = monkey2.calculate()
                operator(op1, op2)
            }
        }

    /**
     * What number do you yell to pass `root`'s equality test?
     */
    override fun part2(input: OperatorMonkey): Long = input
        .apply { resolve() }
        .unknownEquation().let { (monkey, result) ->
            monkey.solve(result)
        }

    private fun Monkey.resolve(): Long? =
        when (this) {
            is NumberMonkey -> number.takeUnless { name == HUMAN }
            is OperatorMonkey -> run {
                number1 = monkey1.resolve()
                number2 = monkey2.resolve()
                operator.takeIf { number1 != null && number2 != null }?.invoke(number1!!, number2!!)
            }
        }

    private fun Monkey.solve(result: Long): Long =
        when (this) {
            is NumberMonkey -> result
            is OperatorMonkey -> unknownEquation().let { (monkey, number) ->
                val monkeyResult = when (operator) {
                    PLUS -> result - number
                    MINUS -> if (monkey == monkey1) result + number else number - result
                    MULTIPLY -> result / number
                    DIVIDE -> if (monkey == monkey1) result * number else result / number
                }
                monkey.solve(monkeyResult)
            }
        }

    sealed interface Monkey {
        val name: String
    }

    data class NumberMonkey(
        override val name: String,
        val number: Long,
    ) : Monkey

    data class OperatorMonkey(
        override val name: String,
        val operator: Operator,
        val name1: String,
        val name2: String,
    ) : Monkey {
        lateinit var monkey1: Monkey
        lateinit var monkey2: Monkey
        var number1: Long? = null
        var number2: Long? = null

        fun unknownEquation(): Pair<Monkey, Long> =
            if (number1 == null) {
                monkey1 to number2!!
            } else {
                monkey2 to number1!!
            }
    }

    enum class Operator(val sign: String, private val operation: (Long, Long) -> Long) {
        PLUS("+", { a, b -> a + b }),
        MINUS("-", { a, b -> a - b }),
        MULTIPLY("*", { a, b -> a * b }),
        DIVIDE("/", { a, b -> a / b });

        operator fun invoke(a: Long, b: Long) = operation(a, b)
    }
}
