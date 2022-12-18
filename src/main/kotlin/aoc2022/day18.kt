package aoc2022

import aoc2022.Day18.Terrain.*
import aoc2022.Point3D.Companion.ONE
import aoc2022.Point3D.Companion.ZERO

/**
 * [Day 18: Boiling Boulders](https://adventofcode.com/2022/day/18)
 */
object Day18 : Puzzle<List<Point3D>, Int> {

    override val name = "🌋🌊⚫ Boiling Boulders"

    override fun parseInput(lines: List<String>): List<Point3D> =
        lines.map { line ->
            line.split(",")
                .map { it.toInt() }
                .let { (x, y, z) -> Point3D(x, y, z) }
        }

    /**
     * What is the surface area of your scanned lava droplet?
     */
    override fun part1(input: List<Point3D>): Int {
        val lava = input.toSet()
        return sumNeighbours(input) { it !in lava }
    }

    /**
     * What is the exterior surface area of your scanned lava droplet?
     */
    override fun part2(input: List<Point3D>): Int {
        val water = pond(input)
            .filter { it.value == WATER }
            .mapTo(mutableSetOf()) { it.point - ONE }
        return sumNeighbours(input) { it in water }
    }

    private fun sumNeighbours(points: List<Point3D>, predicate: (Point3D) -> Boolean): Int =
        points.sumOf { point -> point.neighbours().count(predicate) }

    private fun pond(input: Collection<Point3D>): Grid3D<Terrain> {
        val lava = input.map { it + ONE }.toSet()
        val size = Point3D(
            x = lava.maxOf { it.x },
            y = lava.maxOf { it.y },
            z = lava.maxOf { it.z },
        ) + ONE * 2
        return Grid3D(size) { if (it in lava) LAVA else AIR }.also { flood(it) }
    }

    private fun flood(grid: Grid3D<Terrain>) {
        val queue = ArrayDeque<Point3D>().also { it.add(ZERO) }
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            grid[next] = WATER
            val floodNext = next.neighbours()
                .filter { grid.getOrNull(it)?.value == AIR }
                .filterNot { it in queue }
            queue.addAll(floodNext)
        }
    }

    enum class Terrain {
        AIR, LAVA, WATER
    }
}
