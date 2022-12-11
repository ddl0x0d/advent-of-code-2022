package aoc2022

/**
 * [Day 11: Monkey in the Middle](https://adventofcode.com/2022/day/11)
 */
object Day11 : Puzzle<List<String>, Long> {

    override val name = "🐒🎒🐒 Monkey in the Middle"

    /**
     * What is the level of monkey business after 20 rounds of stuff-slinging simian shenanigans?
     */
    override fun part1(input: List<String>): Long =
        input.toMonkeys { it }.run {
            takeTurns(
                rounds = 20,
                inspect = { operation(it) / 3 },
                testDivisionBy = { it % divisor == 0 },
            )
            business()
        }

    /**
     * What is the level of monkey business after 10000 rounds?
     */
    override fun part2(input: List<String>): Long =
        input.toMonkeys { Item(it) }
            .apply {
                val divisors = map { it.divisor }
                forEach { monkey ->
                    monkey.items.forEach { item ->
                        item.remainders = divisors.associateWith { item.startValue % it }
                    }
                }
            }.run {
                takeTurns(
                    rounds = 10_000,
                    inspect = {
                        it.apply {
                            remainders = remainders.mapValues { (divisor, remainder) ->
                                operation(remainder) % divisor
                            }
                        }
                    },
                    testDivisionBy = { it.remainders[divisor] == 0 },
                )
                business()
            }

    private fun <T> List<String>.toMonkeys(toItem: (Int) -> T): List<Monkey<T>> =
        chunked(7).map { lines ->
            val (startingItems, operation, test, ifTrue, ifFalse) = lines.drop(1)
            Monkey(
                items = startingItems.substringAfter("Starting items: ")
                    .split(", ").mapTo(ArrayDeque()) { toItem(it.toInt()) },
                operation = when {
                    operation.contains("* old") -> Square
                    operation.contains("*") -> Multiply(operation.intAfter("* "))
                    operation.contains("+") -> Add(operation.intAfter("+ "))
                    else -> error("Could not parse operation: '$operation'")
                },
                divisor = test.intAfter("divisible by "),
                trueIndex = ifTrue.intAfter("throw to monkey "),
                falseIndex = ifFalse.intAfter("throw to monkey "),
            )
        }

    private fun String.intAfter(delimiter: String): Int = substringAfter(delimiter).toInt()

    private fun <T> List<Monkey<T>>.takeTurns(
        rounds: Int,
        inspect: Monkey<T>.(item: T) -> T,
        testDivisionBy: Monkey<T>.(item: T) -> Boolean,
    ) {
        repeat(rounds) {
            forEach { monkey ->
                monkey.inspected += monkey.items.size
                while (monkey.items.isNotEmpty()) {
                    val item = monkey.items.removeFirst()
                    val newItem = monkey.inspect(item)
                    val divisible = monkey.testDivisionBy(newItem)
                    val index = if (divisible) monkey.trueIndex else monkey.falseIndex
                    get(index).items.addLast(newItem)
                }
            }
        }
    }

    private fun List<Monkey<*>>.business(): Long =
        map { it.inspected.toLong() }
            .sortedDescending()
            .take(2)
            .reduce(Long::times)

    data class Monkey<T>(
        val items: ArrayDeque<T>,
        val operation: Operation,
        val divisor: Int,
        val trueIndex: Int,
        val falseIndex: Int,
        var inspected: Int = 0
    )

    sealed interface Operation {
        infix operator fun invoke(input: Int): Int
    }

    data class Add(private val value: Int) : Operation {
        override infix fun invoke(input: Int): Int = input + value
    }

    data class Multiply(private val value: Int) : Operation {
        override infix fun invoke(input: Int): Int = input * value
    }

    object Square : Operation {
        override infix fun invoke(input: Int): Int = input * input
    }

    data class Item(
        val startValue: Int,
        var remainders: Map<Int, Int> = emptyMap(),
    )
}
