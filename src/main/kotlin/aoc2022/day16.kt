package aoc2022

import aoc2022.Day16.Valve
import aoc2022.Graph.Node
import kotlin.math.pow

/**
 * [Day 16: Proboscidea Volcanium](https://adventofcode.com/2022/day/16)
 */
object Day16 : Puzzle<Graph<Valve, Int>, Int> {

    override val name = "🌋🐘⚙ Proboscidea Volcanium"

    private const val START_ID = "AA"

    private val regex = "Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.+)".toRegex()

    override fun parseInput(lines: List<String>): Graph<Valve, Int> {
        val valves = lines.map {
            val (id, flowRate, outValves) = regex.matchEntire(it)!!.destructured
            Valve(
                id = id,
                flowRate = flowRate.toInt(),
                outValves = outValves.split(", "),
            )
        }
        val valves2Ids = valves.associateBy { it.id }
        val valveIdPairs = valves.flatMap { valve ->
            valve.outValves.map { valve.id to it }
        }
        return Graph.from(valveIdPairs) { (from, to) ->
            Triple(valves2Ids.getValue(from), valves2Ids.getValue(to), 1)
        }
    }

    /**
     * What is the most pressure you can release?
     */
    override fun part1(input: Graph<Valve, Int>): Int {
        val start = input.first { it.value.id == START_ID }
        val working = input.filterNot { it.value.damaged }
        val initial = Route(minutesLeft = 30, valves = working)
        val best = bestRoute(from = start, current = initial)
        return best.releasePotential
    }

    /**
     * With you and an 🐘 working together for 26 minutes, what is the most pressure you could release?
     */
    override fun part2(input: Graph<Valve, Int>): Int {
        val start = input.first { it.value.id == START_ID }
        val working = input.filterNot { it.value.damaged }.sortedBy { it.value.id }
        val combinations = 2.0.pow(working.size.toDouble()).toInt()
        val result = (0 until combinations).maxOf {
            val myNodes = mutableListOf<Node<Valve, *>>()
            val elephantNodes = mutableListOf<Node<Valve, *>>()
            val combo = it.toString(2).padStart(working.size, '0')
            combo.withIndex().forEach { (index, char) ->
                val nodes = if (char == '0') myNodes else elephantNodes
                nodes += working[index]
            }
            val myBest = bestRoute(start, Route(minutesLeft = 26, valves = myNodes))
            val elephantBest = bestRoute(start, Route(minutesLeft = 26, valves = elephantNodes))
            myBest.releasePotential + elephantBest.releasePotential
        }
        return result
    }

    private fun bestRoute(from: Node<Valve, *>, current: Route, bestKnown: Route = current): Route =
        distances(from, current.valves.filterNot { it.value.opened })
            .mapValues { (node, distance) ->
                current.open(distance, node)
            }.filter { (_, route) ->
                route.minutesLeft >= 0// && route > bestKnown // powerful optimization, yet wrong for part 2 🤔
            }.entries.fold(bestKnown) { theBest, (node, state) ->
                node.value.opened = true
                val best = bestRoute(node, state, maxOf(state, theBest))
                node.value.opened = false
                maxOf(best, theBest)
            }

    private fun distances(from: Node<Valve, *>, toNodes: List<Node<Valve, *>>): Map<Node<Valve, *>, Int> =
        toNodes.filter { it != from }.associateWith { to -> distance(from, to) }

    private fun distance(from: Node<Valve, *>, to: Node<Valve, *>): Int {
        val key = listOf(from, to).map { it.value.id }.sorted().joinToString("-")
        return distanceCache.getOrPut(key) { shortestPath(from, to)!!.size }
    }

    private val distanceCache = mutableMapOf<String, Int>()

    data class Route(
        val minutesLeft: Int,
        val released: Int = 0,
        val releaseRate: Int = 0,
        val valves: List<Node<Valve, *>>,
        val visited: List<Node<Valve, *>> = emptyList()
    ) : Comparable<Route> {

        val releasePotential = released + minutesLeft * releaseRate

        fun open(distance: Int, node: Node<Valve, *>): Route =
            move(distance).open(node)

        private fun move(distance: Int = 1): Route = copy(
            minutesLeft = minutesLeft - distance,
            released = released + distance * releaseRate,
        )

        private fun open(node: Node<Valve, *>): Route = copy(
            minutesLeft = minutesLeft - 1,
            releaseRate = releaseRate + node.value.flowRate,
            released = released + releaseRate,
            visited = visited + node
        )

        override fun compareTo(other: Route): Int = releasePotential - other.releasePotential
    }

    data class Valve(
        val id: String,
        val flowRate: Int,
        val outValves: List<String>,
        var opened: Boolean = false
    ) {
        val damaged = flowRate == 0
    }
}
