package aoc2022

import aoc2022.IGrid.Cell

/**
 * [Day 12: Hill Climbing Algorithm](https://adventofcode.com/2022/day/12)
 */
object Day12 : Puzzle<Graph<Cell<Char, Point>, Int>, Int> {

    override val name = "⛰🥾📻 Hill Climbing Algorithm"

    private lateinit var start: Point
    private lateinit var end: Point

    override fun parseInput(lines: List<String>): Graph<Cell<Char, Point>, Int> {
        val grid = Grid.read(lines) { char, point ->
            when (char) {
                'S' -> 'a'.also { start = point }
                'E' -> 'z'.also { end = point }
                else -> char
            }
        }
        val steps = grid.flatMap { cell ->
            grid.neighbours(cell.point) {
                it.value <= cell.value || it.value == cell.value + 1
            }.map { cell to it }
        }
        return Graph.from(steps) { (from, to) -> Triple(from, to, 1) }
    }

    /**
     * What is the fewest steps required to move from your current position to the location that should get the best signal?
     */
    override fun part1(input: Graph<Cell<Char, Point>, Int>): Int {
        val start = input.first { it.value.point == start }
        val end = input.first { it.value.point == end }
        return shortestPath(start, end)?.size ?: error("Could not find any paths")
    }

    /**
     * What is the fewest steps required to move starting from any square with elevation `a` to the location that should get the best signal?
     */
    override fun part2(input: Graph<Cell<Char, Point>, Int>): Int {
        val starts = input.filter { it.value.value == 'a' }
        val end = input.first { it.value.point == end }
        return starts.mapNotNull { shortestPath(it, end) }.minOf { it.size }
    }
}
