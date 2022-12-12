package aoc2022

import aoc2022.Graph.Node

data class Graph<NODE, EDGE>(private val nodes: Set<Node<NODE, EDGE>>) : Iterable<Node<NODE, EDGE>> {

    data class Node<NODE, EDGE>(val value: NODE) {
        val inbound = mutableMapOf<Node<NODE, EDGE>, EDGE>()
        val outbound = mutableMapOf<Node<NODE, EDGE>, EDGE>()

        fun join(other: Node<NODE, EDGE>, weight: EDGE) {
            this.outbound[other] = weight
            other.inbound[this] = weight
        }
    }

    override fun iterator(): Iterator<Node<NODE, EDGE>> = nodes.iterator()

    companion object {
        fun <T, NODE, EDGE> from(
            input: Iterable<T>,
            parse: (T) -> Triple<NODE, NODE, EDGE>
        ): Graph<NODE, EDGE> {
            val nodes = mutableMapOf<NODE, Node<NODE, EDGE>>()
            input.forEach {
                val (from, to, weight) = parse(it)
                val nodeFrom = nodes.getOrPut(from) { Node(from) }
                val nodeTo = nodes.getOrPut(to) { Node(to) }
                nodeFrom.join(nodeTo, weight)
            }
            return Graph(nodes.values.toSet())
        }
    }
}

fun shortestPath(
    from: Node<*, *>, to: Node<*, *>,
    path: List<Node<*, *>> = emptyList(),
): List<Node<*, *>>? =
    if (from == to) {
        path
    } else {
        from.outbound.keys
            .filterNot { it in path }
            .mapNotNull { shortestPath(it, to, path + it) }
            .minByOrNull { it.size }
    }

