package aoc2022

class Grid<T>(private val cells: List<List<T>>) {
    val rows = cells.size
    val cols = cells.first().size

    operator fun get(row: Int): List<T> = cells[row]
    operator fun get(point: Point): T = cells[point.y][point.x]

    companion object {
        fun <T> from(lines: List<String>, toCell: (Char) -> T): Grid<T> =
            Grid(lines.map { line -> line.map { toCell(it) } })
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

fun <T, U> Grid<T>.flatten(transform: (row: Int, col: Int) -> U): List<U> =
    (0 until rows).flatMap { row ->
        (0 until cols).map { col ->
            transform(row, col)
        }
    }

fun <T> Grid<T>.slice(rows: IntRange, cols: IntRange) = Grid(
    rows.map { row ->
        cols.map { col ->
            this[row][col]
        }
    }
)
