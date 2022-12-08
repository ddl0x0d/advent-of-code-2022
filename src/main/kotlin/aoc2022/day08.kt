package aoc2022

import aoc2022.Direction.*

/**
 * [Day 8: Treetop Tree House](https://adventofcode.com/2022/day/8)
 */
object Day8 : Puzzle<Grid<Int>, Int> {

    override val name = "🌳🏚🌳 Treetop Tree House"

    /**
     * 2-dimensional map with the height of each tree
     */
    override fun parseInput(lines: List<String>) = Grid.from(lines) { it.digitToInt() }

    /**
     * How many trees are visible from outside the grid?
     */
    override fun part1(input: Grid<Int>): Int = input.treesVisibleOnEdge() + input.treesVisibleInInterior()

    private fun Grid<Int>.treesVisibleOnEdge(): Int = 2 * (rows + cols - 2)

    private fun Grid<Int>.treesVisibleInInterior() =
        interiorTreePoints().count { point ->
            Direction.values().firstOrNull { direction ->
                treesToEdge(point, direction).all { it < get(point) }
            } != null
        }

    /**
     * What is the highest scenic score possible for any tree?
     */
    override fun part2(input: Grid<Int>): Int =
        input.interiorTreePoints().maxOf { point ->
            Direction.values().map { direction ->
                input.treesToEdge(point, direction).viewingDistance(input[point])
            }.reduce(Int::times)
        }

    private fun Grid<Int>.interiorTreePoints(): List<Point> =
        slice(1 until rows - 1, 1 until cols - 1)
            .flatten { row, col -> Point(row + 1 to col + 1) }

    private fun Grid<Int>.treesToEdge(start: Point, direction: Direction): Sequence<Int> {
        val treesLeft = when (direction) {
            TOP -> start.row
            RIGHT -> cols - start.col - 1
            BOTTOM -> rows - start.row - 1
            LEFT -> start.col
        }
        return generateSequence(start) { it + direction.point }.drop(1).take(treesLeft).map(::get)
    }

    private fun Sequence<Int>.viewingDistance(tree: Int): Int =
        indexOfFirst { it >= tree }.takeIf { it > -1 }?.plus(1) ?: count()
}
