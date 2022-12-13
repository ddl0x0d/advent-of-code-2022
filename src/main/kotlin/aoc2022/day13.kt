package aoc2022

/**
 * [Day 13: Distress Signal](https://adventofcode.com/2022/day/13)
 */
object Day13 : Puzzle<List<String>, Int> {

    override val name = "🆘📻🆘 Distress Signal"

    /**
     * What is the sum of the indices of those pairs?
     */
    override fun part1(input: List<String>): Int =
        input.chunked(3).map { (left, right) ->
            left.toPacket() to right.toPacket()
        }.mapIndexedNotNull { index, (left, right) ->
            val compare = comparePackets(left, right)
            if (compare == true) index + 1 else null
        }.sum()

    /**
     * What is the decoder key for the distress signal?
     */
    override fun part2(input: List<String>): Int {
        val packets = input.filter { it.isNotEmpty() }.map { it.toPacket() }
        val div2 = ListPacket(listOf(ListPacket(listOf(IntPacket(2)))))
        val div6 = ListPacket(listOf(ListPacket(listOf(IntPacket(6)))))
        val result = (packets + div2 + div6).sortedWith { p1, p2 ->
            when (comparePackets(p1, p2)) {
                false -> 1
                null -> 0
                true -> -1
            }
        }
        val i1 = result.indexOf(div2) + 1
        val i2 = result.indexOf(div6) + 1
        return i1 * i2
    }

    private fun String.toPacket(): Packet {
        var packet = ListPacket(emptyList())
        val currentLists = ArrayDeque<MutableList<Packet>>()
        var currentInteger = 0
        var lastChar = ' '
        forEach { char ->
            when (char) {
                '[' -> currentLists.addLast(mutableListOf())

                ']' -> {
                    val currentList = currentLists.removeLast()
                    currentList += when (lastChar) {
                        ']' -> packet
                        else -> IntPacket(currentInteger).also { currentInteger = 0 }
                    }
                    packet = ListPacket(currentList)
                }

                ',' -> currentLists.last() += when (lastChar) {
                    ']' -> packet
                    else -> IntPacket(currentInteger).also { currentInteger = 0 }
                }

                else -> currentInteger = currentInteger * 10 + char.digitToInt()
            }
            lastChar = char
        }
        return packet
    }

    private fun comparePackets(left: Packet, right: Packet): Boolean? =
        when {
            left is IntPacket && right is IntPacket -> when {
                left.value < right.value -> true
                left.value == right.value -> null
                else -> false
            }

            left is ListPacket && right is ListPacket -> {
                (left.list zip right.list)
                    .map { (l, r) -> comparePackets(l, r) }
                    .firstOrNull { it != null }
                    ?: comparePackets(IntPacket(left.list.size), IntPacket(right.list.size))
            }

            left is IntPacket && right is ListPacket -> comparePackets(ListPacket(listOf(left)), right)
            left is ListPacket && right is IntPacket -> comparePackets(left, ListPacket(listOf(right)))
            else -> error("Impossible condition")
        }

    sealed interface Packet

    data class IntPacket(val value: Int) : Packet {
        override fun toString(): String = value.toString()
    }

    data class ListPacket(val list: List<Packet>) : Packet {
        override fun toString(): String = list.joinToString(",", "[", "]")
    }
}
