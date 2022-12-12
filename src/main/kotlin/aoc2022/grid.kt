package aoc2022

import aoc2022.Grid.Cell

class Grid<T>(private val cells: List<List<Cell<T>>>) : Iterable<Cell<T>> {
    val rows = cells.size
    val cols = cells.first().size

    operator fun get(row: Int, col: Int): Cell<T> = cells[row][col]
    operator fun get(point: Point): Cell<T> = cells[point.y][point.x]

    data class Cell<T>(val value: T, val point: Point)

    override fun iterator(): Iterator<Cell<T>> = GridIterator()

    private inner class GridIterator(private var index: Int = 0) : Iterator<Cell<T>> {
        override fun hasNext(): Boolean = index < rows * cols
        override fun next(): Cell<T> = this@Grid[index / cols, index % cols].also { index++ }
    }

    companion object {
        fun <T> from(lines: List<String>, transform: (Char, Point) -> T): Grid<T> =
            Grid(
                lines.mapIndexed { row, line ->
                    line.mapIndexed { col, char ->
                        val point = Point(col, row)
                        val value = transform(char, point)
                        Cell(value, point)
                    }
                }
            )
    }
}

data class Point(val x: Int, val y: Int) {

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    fun map(transform: (Int) -> Int) = Point(transform(x), transform(y))

    companion object {
        val ZERO = Point(0, 0)
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0);

    val point = Point(x, y)
}

fun <T> Grid<T>.neighbours(point: Point): List<Cell<T>> =
    Direction.values()
        .map { point + it.point }
        .filter { it.x >= 0 && it.y >= 0 && it.x < cols && it.y < rows }
        .map { this[it] }

fun <T> Grid<T>.slice(rows: IntRange, cols: IntRange) = Grid(
    rows.map { row ->
        cols.map { col ->
            this[row, col]
        }
    }
)
