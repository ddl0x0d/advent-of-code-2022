package aoc2022

import kotlin.math.absoluteValue

/**
 * [Day 20: Grove Positioning System](https://adventofcode.com/2022/day/20)
 */
object Day20 : Puzzle<List<Int>, Long> {

    override val name = "🌳🌟🧭 Grove Positioning System"

    private const val DECRYPTION_KEY = 811_589_153

    override fun parseInput(lines: List<String>): List<Int> {
        return lines.map { it.toInt() }
    }

    /**
     * What is the sum of the three numbers that form the grove coordinates?
     */
    override fun part1(input: List<Int>): Long = groveCoordinates(input, mix = 1)

    /**
     * What is the sum of the three numbers that form the grove coordinates?
     */
    override fun part2(input: List<Int>): Long = groveCoordinates(input.map { it.toLong() * DECRYPTION_KEY }, mix = 10)

    private fun groveCoordinates(nums: List<Number>, mix: Int): Long {
        val nodes = nums.toNodes()
        repeat(mix) { mix(nodes) }
        val zeroNode = nodes.first { it.value == 0L }
        return (1..3).map { index ->
            generateSequence(zeroNode) { it.next }
                .drop(index * 1000 % nodes.size)
                .first().value
        }.reduce(Long::plus)
    }

    private fun List<Number>.toNodes(): List<Node> =
        map { Node(it.toLong()) }.apply {
            windowed(2) { (prev, next) ->
                prev.next = next
                next.prev = prev
            }
            val first = first()
            val last = last()
            first.prev = last
            last.next = first
        }

    private fun mix(nodes: List<Node>) {
        nodes.forEach { node ->
            val times = node.value.absoluteValue % (nodes.size - 1)
            when {
                node.value < 0 -> {
                    repeat(times.toInt()) {
                        swap(node.prev, node)
                    }
                }

                node.value > 0 -> {
                    repeat(times.toInt()) {
                        swap(node, node.next)
                    }
                }
            }
        }
    }

    private fun swap(a: Node, b: Node) {
        val node1 = a.prev
        val node2 = a
        val node3 = b
        val node4 = b.next

        node1.next = node3
        node2.prev = node3
        node2.next = node4
        node3.prev = node1
        node3.next = node2
        node4.prev = node2
    }

    data class Node(val value: Long) {
        lateinit var prev: Node
        lateinit var next: Node
    }
}
