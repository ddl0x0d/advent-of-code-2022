package aoc2022

import aoc2022.Direction.*
import aoc2022.Point.Companion.ONE

/**
 * [Day 8: Treetop Tree House](https://adventofcode.com/2022/day/8)
 */
object Day8 : Puzzle<Grid<Int>, Int> {

    override val name = "🌳🏚🌳 Treetop Tree House"

    /**
     * 2-dimensional map with the height of each tree
     */
    override fun parseInput(lines: List<String>) = Grid.read(lines) { char, _ -> char.digitToInt() }

    /**
     * How many trees are visible from outside the grid?
     */
    override fun part1(input: Grid<Int>): Int = input.treesVisibleOnEdge() + input.treesVisibleInInterior()

    private fun Grid<Int>.treesVisibleOnEdge(): Int = 2 * (size.x + size.y - 2)

    private fun Grid<Int>.treesVisibleInInterior() =
        interiorTreePoints().count { point ->
            Direction.values().firstOrNull { direction ->
                treesToEdge(point, direction).all { it < get(point).value }
            } != null
        }

    /**
     * What is the highest scenic score possible for any tree?
     */
    override fun part2(input: Grid<Int>): Int =
        input.interiorTreePoints().maxOf { point ->
            Direction.values().map { direction ->
                input.treesToEdge(point, direction).viewingDistance(input[point].value)
            }.reduce(Int::times)
        }

    private fun Grid<Int>.interiorTreePoints(): List<Point> =
        slice(1 until size.y - 1, 1 until size.x - 1).map { it.point + ONE }

    private fun Grid<Int>.treesToEdge(start: Point, direction: Direction): Sequence<Int> {
        val treesLeft = when (direction) {
            UP -> start.y
            RIGHT -> size.x - start.x - 1
            DOWN -> size.y - start.y - 1
            LEFT -> start.x
        }
        return generateSequence(start) { it + direction.gridPoint }
            .drop(1).take(treesLeft).map(::get).map { it.value }
    }

    private fun Sequence<Int>.viewingDistance(tree: Int): Int =
        indexOfFirst { it >= tree }.takeIf { it > -1 }?.plus(1) ?: count()
}
