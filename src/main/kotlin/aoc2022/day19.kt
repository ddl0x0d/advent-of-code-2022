package aoc2022

import aoc2022.Day19.Blueprint
import aoc2022.Day19.Resource.*
import kotlin.math.ceil
import kotlin.math.max

/**
 * [Day 19: Not Enough Minerals](https://adventofcode.com/2022/day/19)
 */
object Day19 : Puzzle<List<Blueprint>, Int> {

    override val name = "💎⛏💎 Not Enough Minerals"

    private const val int = "(\\d+)"
    private val regex = ("Blueprint $int: " +
            "Each ore robot costs $int ore. " +
            "Each clay robot costs $int ore. " +
            "Each obsidian robot costs $int ore and $int clay. " +
            "Each geode robot costs $int ore and $int obsidian.").toRegex()

    override fun parseInput(lines: List<String>): List<Blueprint> =
        lines.map { line ->
            val groups = regex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
            var index = 0
            Blueprint(
                id = groups[index++],
                costs = mapOf(
                    ORE to mapOf(ORE to groups[index++]),
                    CLAY to mapOf(ORE to groups[index++]),
                    OBSIDIAN to mapOf(ORE to groups[index++], CLAY to groups[index++]),
                    GEODE to mapOf(ORE to groups[index++], OBSIDIAN to groups[index]),
                )
            )
        }

    /**
     * What do you get if you add up the quality level of all the blueprints in your list?
     */
    override fun part1(input: List<Blueprint>): Int =
        input.associateWith {
            maxGeodes(RobotFactory(blueprint = it, minuteLimit = 24))
        }.entries.sumOf { (blueprint, maxGeodes) ->
            blueprint.id * maxGeodes
        }

    /**
     * What do you get if you multiply the largest number of geodes together?
     */
    override fun part2(input: List<Blueprint>): Int =
        input.take(3).map {
            maxGeodes(RobotFactory(blueprint = it, minuteLimit = 32))
        }.reduce(Int::times)

    private fun maxGeodes(factory: RobotFactory): Int {
        var maxGeodes = 0
        val queue = ArrayDeque<RobotFactory>().also { it.add(factory) }
        val hashes = mutableSetOf<Long>()
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val timeLeft = current.minuteLimit - current.minute
            if (timeLeft >= 0) {
                val ttp = current.timeToProduce()
                val robots = ttp.filter { it.value == 0 }.keys
                val options = if (GEODE in robots) {
                    listOf(current.produce(GEODE))
                } else {
                    val produced = robots.map { current.produce(it) }
                    produced + current.collect()
                }
                options
                    .filter { hashes.add(it.hash()) }
                    .filter { it.minTimeToProduce(maxGeodes, GEODE) <= timeLeft }
                    .reversed().forEach { queue.addFirst(it) }
            } else {
                maxGeodes = max(maxGeodes, current.resources.getValue(GEODE))
            }
        }
        println("Blueprint #${factory.blueprint.id} can produce at most $maxGeodes geodes")
        return maxGeodes
    }

    data class RobotFactory(
        val blueprint: Blueprint,
        val minuteLimit: Int,
        val minute: Int = 1,
        val robots: Map<Resource, Int> = mapOf(ORE to 1, CLAY to 0, OBSIDIAN to 0, GEODE to 0),
        val resources: Map<Resource, Int> = mapOf(ORE to 0, CLAY to 0, OBSIDIAN to 0, GEODE to 0),
    ) {
        fun hash(): Long = (robots.values + resources.values)
            .fold(minute.toLong()) { result, value -> result * 1000 + value }

        fun minTimeToProduce(required: Int, robot: Resource): Int {
            if (required == 0) {
                return 0
            }
            var firstRobotTime = 0
            var remaining = required - resources.getValue(robot)
            var productionSpeed = robots.getValue(robot)
            if (productionSpeed == 0) {
                firstRobotTime = blueprint.costs[robot]!!.entries.maxOf { (robot, cost) ->
                    minTimeToProduce(cost, robot)
                }
            }
            var time = 0
            var accelerate = 1
            while (remaining > 0) {
                time++
                remaining -= productionSpeed
                productionSpeed += accelerate
                if (time > 5) {
                    accelerate = 0
                }
            }
            return time + firstRobotTime
        }

        @Suppress("UNCHECKED_CAST")
        fun timeToProduce(): Map<Resource, Int> =
            Resource.values().reversed()
                .associateWith { timeToProduce(it) }
                .filterValues { it != null && it < minuteLimit - minute } as Map<Resource, Int>

        private fun timeToProduce(robot: Resource): Int? =
            when (robot) {
                ORE -> timeToProduce(ORE, ORE)
                CLAY -> timeToProduce(CLAY, ORE)
                OBSIDIAN -> if (robots.getValue(CLAY) > 0) max(
                    timeToProduce(OBSIDIAN, CLAY),
                    timeToProduce(OBSIDIAN, ORE)
                ) else null

                GEODE -> if (robots.getValue(OBSIDIAN) > 0) max(
                    timeToProduce(GEODE, OBSIDIAN),
                    timeToProduce(GEODE, ORE)
                ) else null
            }

        private fun timeToProduce(robot: Resource, resource: Resource): Int {
            val cost = blueprint.cost(robot, resource)
            val resources = resources.getValue(resource)
            val robots = robots.getValue(robot)
            return ceil(max(0, cost - resources).toDouble() / robots).toInt()
        }

        fun produce(robot: Resource, minutes: Int = 0): RobotFactory {
            var next = this
            repeat(minutes) {
                next = next.collect()
            }
            return next.spend(robot).collect().add(robot)
        }

        fun collect(minutes: Int = 1): RobotFactory = copy(
            minute = minute + minutes,
            resources = resources.mapValues { (resource, amount) -> robots.getValue(resource) * minutes + amount }
        )

        private fun spend(robot: Resource): RobotFactory = copy(
            resources = resources + when (robot) {
                ORE -> mapOf(spend(ORE, ORE))
                CLAY -> mapOf(spend(CLAY, ORE))
                OBSIDIAN -> mapOf(spend(OBSIDIAN, ORE), spend(OBSIDIAN, CLAY))
                GEODE -> mapOf(spend(GEODE, ORE), spend(GEODE, OBSIDIAN))
            }
        )

        private fun spend(robot: Resource, resource: Resource): Pair<Resource, Int> =
            resource to (resources.getValue(resource) - blueprint.cost(robot, resource))

        private fun add(robot: Resource): RobotFactory = copy(
            robots = robots + (robot to robots.getValue(robot) + 1)
        )
    }

    class Blueprint(val id: Int, val costs: Map<Resource, Map<Resource, Int>>) {
        fun cost(robot: Resource, resource: Resource): Int =
            costs.getValue(robot).getValue(resource)
    }

    enum class Resource {
        ORE, CLAY, OBSIDIAN, GEODE
    }
}
