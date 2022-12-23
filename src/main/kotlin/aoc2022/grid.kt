package aoc2022

import aoc2022.IGrid.Cell


abstract class IGrid<P : IPoint<P>, T>(val size: P, val transform: (P) -> T) : Iterable<Cell<T, P>> {

    private val cells: List<Cell<T, P>> =
        List(size.coordinates.reduce(Int::times)) {
            val point = indexToPoint(it)
            Cell(transform(point), point)
        }

    operator fun contains(point: P): Boolean =
        (point zip size).all { (p, s) -> p in (0 until s) }

    fun getOrNull(point: P): Cell<T, P>? =
        if (contains(point)) get(point) else null

    operator fun get(point: P): Cell<T, P> = cells[pointToIndex(point)]

    operator fun set(point: P, value: T) {
        get(point).value = value
    }

    override fun iterator(): Iterator<Cell<T, P>> = cells.iterator()

    abstract fun indexToPoint(index: Int): P
    abstract fun pointToIndex(point: P): Int

    data class Cell<T, P>(var value: T, val point: P)
}

class Grid<T>(size: Point, transform: (Point) -> T) : IGrid<Point, T>(size, transform) {

    constructor(rows: Int, cols: Int, transform: (Point) -> T) : this(Point(cols, rows), transform)

    override fun indexToPoint(index: Int) = Point(index % size.x, index / size.x)
    override fun pointToIndex(point: Point): Int = point.let { (x, y) -> y * size.x + x }

    companion object {
        fun <T> read(lines: List<String>, transform: (Char, Point) -> T) =
            Grid(lines.size, lines.first().length) { point ->
                val (x, y) = point
                val char = lines[y][x]
                transform(char, point)
            }
    }
}

class Grid3D<T>(size: Point3D, transform: (Point3D) -> T) : IGrid<Point3D, T>(size, transform) {

    override fun indexToPoint(index: Int) = Point3D(
        x = index / (size.y * size.z),
        y = index % (size.y * size.z) / size.z,
        z = index % size.z
    )

    override fun pointToIndex(point: Point3D): Int =
        point.let { (x, y, z) -> x * size.y * size.z + y * size.z + z }
}

interface IPoint<T : IPoint<T>> {
    val coordinates: List<Int>

    operator fun plus(other: T): T = merge(other, Int::plus)
    operator fun minus(other: T): T = merge(other, Int::minus)
    operator fun times(times: Int): T = map { it * times }

    fun map(transform: (Int) -> Int): T = create(coordinates.map(transform))

    fun merge(other: T, merge: (Int, Int) -> Int): T = create(zip(other).map { (a, b) -> merge(a, b) })

    infix fun zip(other: T): List<Pair<Int, Int>> = coordinates zip other.coordinates

    fun create(coordinates: List<Int>): T
}

data class Point(val x: Int, val y: Int) : IPoint<Point> {

    override val coordinates = listOf(x, y)

    override fun create(coordinates: List<Int>) = coordinates.let { (x, y) -> Point(x, y) }

    fun neighbours(): List<Point> = Direction.values().map { this + it.point }

    companion object {
        val ZERO = Point(0, 0)
        val ONE = Point(1, 1)
    }
}

enum class Direction(val x: Int, val y: Int) {
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1),
    LEFT(-1, 0);

    val point = Point(x, y)
    val gridPoint = Point(x, -y)
}

data class Point3D(val x: Int, val y: Int, val z: Int) : IPoint<Point3D> {

    override val coordinates = listOf(x, y, z)

    override fun create(coordinates: List<Int>) = coordinates.let { (x, y, z) -> Point3D(x, y, z) }

    fun neighbours(): List<Point3D> = unitVectors.map { this + it }

    companion object {

        val ZERO = Point3D(0, 0, 0)
        val ONE = Point3D(1, 1, 1)

        val unitVectors = listOf(
            Point3D(-1, 0, 0),
            Point3D(1, 0, 0),
            Point3D(0, -1, 0),
            Point3D(0, 1, 0),
            Point3D(0, 0, -1),
            Point3D(0, 0, 1),
        )
    }
}

fun <T> Grid<T>.neighbours(point: Point): List<Cell<T, Point>> =
    point.neighbours().filter { contains(it) }.map { get(it) }

fun <T> Grid<T>.expand(size: Point, newCell: (Point) -> T): Grid<T> =
    Grid(size) {
        if (it in this) {
            this[it].value
        } else {
            newCell(it)
        }
    }

fun <T> Grid<T>.slice(rows: IntRange, cols: IntRange): Grid<T> {
    val offset = Point(cols.first, rows.first)
    return Grid(rows.size, cols.size) { this[it + offset].value }
}

fun <T> Grid<T>.print(transform: (Cell<T, Point>) -> Char) {
    forEach { cell ->
        print(transform(cell))
        if (cell.point.x == size.x - 1) {
            print(" ${cell.point.y}")
            println()
        }
    }
}
