package aoc2022

class Grid<T>(private val cells: List<List<T>>) {
    val rows = cells.size
    val cols = cells.first().size

    operator fun get(row: Int): List<T> = cells[row]
    operator fun get(point: Point): T = cells[point.row][point.col]

    companion object {
        fun <T> from(lines: List<String>, toCell: (Char) -> T): Grid<T> =
            Grid(lines.map { line -> line.map { toCell(it) } })
    }
}

@JvmInline
value class Point(private val pair: Pair<Int, Int>) {
    val row: Int get() = pair.first
    val col: Int get() = pair.second

    operator fun component1() = row
    operator fun component2() = col

    operator fun plus(other: Point) = Point(row + other.row to col + other.col)
}

enum class Direction(row: Int, col: Int) {
    TOP(-1, 0),
    RIGHT(0, 1),
    BOTTOM(1, 0),
    LEFT(0, -1);

    val point = Point(row to col)
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
