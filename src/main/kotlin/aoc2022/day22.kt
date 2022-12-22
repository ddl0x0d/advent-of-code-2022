package aoc2022

import aoc2022.Day22.*
import aoc2022.Day22.Cube.Connection
import aoc2022.Day22.Terrain.*
import aoc2022.Day22.Turn.*
import aoc2022.Direction.*
import aoc2022.Point.Companion.ONE

/**
 * [Day 22: Monkey Map](https://adventofcode.com/2022/day/22)
 */
class Day22(private val cube: Cube) : Puzzle<Pair<List<Move>, Grid<Terrain>>, Int> {

    override val name = "🐒🗺🐒 Monkey Map"

    override fun parseInput(lines: List<String>): Pair<List<Move>, Grid<Terrain>> {
        val separatorIndex = lines.indexOfFirst { it.isEmpty() }
        val mapLines = lines.subList(0, separatorIndex)
        return parseMoves(lines.last()) to parseMap(mapLines)
    }

    private fun parseMoves(line: String): List<Move> =
        buildList {
            var steps = 0
            var turn = TURN_NOT
            line.forEach { char ->
                if (char.isDigit()) {
                    steps = steps * 10 + char.digitToInt()
                } else {
                    add(Move(turn, steps))
                    turn = Turn.values().first { it.char == char }
                    steps = 0
                }
            }
            add(Move(turn, steps))
        }

    private fun parseMap(lines: List<String>): Grid<Terrain> {
        val maxLength = lines.maxOf { it.length }
        val paddedLines = lines.map { it.padEnd(maxLength) }
        return Grid.read(paddedLines) { char, _ ->
            Terrain.values().first { it.char == char }
        }
    }

    /**
     * If a movement instruction took you off of the map, you wrap around to the other side of the board.
     * What is the final password?
     */
    override fun part1(input: Pair<List<Move>, Grid<Terrain>>): Int =
        input.let { (_, map) ->
            followPath(input) { position ->
                position.move(if (position.point in map) ONE else map.size * -1)
            }.finalPassword()
        }

    /**
     * Now, if you would walk off the board, you instead proceed around the cube.
     * What is the final password?
     */
    override fun part2(input: Pair<List<Move>, Grid<Terrain>>): Int =
        followPath(input) { cube.wrap(it) }.finalPassword()

    private fun followPath(input: Pair<List<Move>, Grid<Terrain>>, wrap: (Position) -> Position): Position {
        val (moves, map) = input
        val start = map.first { it.value == OPEN }.point
        return moves.fold(Position(start, RIGHT)) { position, move ->
            var next = position.turn(move.turn)
            var stepsLeft = move.steps
            var now: Position
            do {
                now = next
                next = now.move()
                while (next.point !in map || map[next.point].value == EMPTY) {
                    next = wrap(next)
                }
            } while (stepsLeft-- > 0 && map[next.point].value != WALL)
            now
        }
    }

    data class Position(val point: Point, val direction: Direction) {
        fun move(steps: Point = ONE) = copy(point = point + direction.gridPoint * steps)
        fun turn(turn: Turn) = copy(direction = direction + turn)
        fun finalPassword(): Int {
            val row = point.y + 1
            val column = point.x + 1
            val facing = listOf(RIGHT, DOWN, LEFT, UP).indexOf(direction)
            return 1000 * row + 4 * column + facing
        }
    }

    enum class Terrain(val char: Char) {
        EMPTY(' '),
        OPEN('.'),
        WALL('#'),
    }

    enum class Turn(val char: Char, val vector: Int) {
        TURN_LEFT('L', -1),
        TURN_RIGHT('R', 1),
        TURN_AROUND('A', 2),
        TURN_NOT('X', 0),
    }

    data class Move(val turn: Turn, val steps: Int)

    data class Cube(
        private val size: Int,
        private val edges: Grid<Int>,
        private val connections: List<Connection>,
    ) {
        fun wrap(position: Position): Position {
            val point = position.point - position.direction.gridPoint
            val edge = edges[point.map { it / size }].value
            val connection = connections.first { it.sourceEdge == edge && it.sourceDirection == position.direction }
            return connection.wrap(point)
        }

        private fun Connection.wrap(point: Point): Position {
            val base = edges.first { it.value == targetEdge }.point * size
            val offset = point.map { it % size }
            val fixed = when (targetDirection) {
                //@formatter:off
                LEFT  -> Point(base.x, 0)
                RIGHT -> Point(base.x + size - 1, 0)
                UP    -> Point(0, base.y)
                DOWN  -> Point(0, base.y + size - 1)
                //@formatter:on
            }
            val wrappedDirection = targetDirection + TURN_AROUND
            val turn = wrappedDirection - sourceDirection
            val dynamic = when { // TODO: refactor
                //@formatter:off
                targetDirection == LEFT  && turn == TURN_LEFT   -> Point(0, base.y + size - 1 - offset.x)
                targetDirection == LEFT  && turn == TURN_RIGHT  -> Point(0, base.y + offset.x)
                targetDirection == LEFT  && turn == TURN_AROUND -> Point(0, base.y + size - 1 - offset.y)
                targetDirection == LEFT  && turn == TURN_NOT    -> Point(0, base.y + offset.y)
                targetDirection == RIGHT && turn == TURN_LEFT   -> Point(0, base.y + size - 1 - offset.x)
                targetDirection == RIGHT && turn == TURN_RIGHT  -> Point(0, base.y + offset.x)
                targetDirection == RIGHT && turn == TURN_AROUND -> Point(0, base.y + size - 1 - offset.y)
                targetDirection == RIGHT && turn == TURN_NOT    -> Point(0, base.y + offset.y)
                targetDirection == UP    && turn == TURN_LEFT   -> Point(base.x + offset.y, 0)
                targetDirection == UP    && turn == TURN_RIGHT  -> Point(base.x + size - 1 - offset.y, 0)
                targetDirection == UP    && turn == TURN_AROUND -> Point(base.x + size - 1 - offset.x, 0)
                targetDirection == UP    && turn == TURN_NOT    -> Point(base.x + offset.x, 0)
                targetDirection == DOWN  && turn == TURN_LEFT   -> Point(base.x + offset.y, 0)
                targetDirection == DOWN  && turn == TURN_RIGHT  -> Point(base.x + size - 1 - offset.y, 0)
                targetDirection == DOWN  && turn == TURN_AROUND -> Point(base.x + size - 1 - offset.x, 0)
                targetDirection == DOWN  && turn == TURN_NOT    -> Point(base.x + offset.x, 0)
                //@formatter:on
                else -> error("Unknown movement")
            }
            val wrappedPoint = fixed + dynamic
            return Position(wrappedPoint, wrappedDirection)
        }

        data class Connection(
            val sourceEdge: Int,
            val sourceDirection: Direction,
            val targetEdge: Int,
            val targetDirection: Direction,
        )
    }

    companion object {

        private val numDirections = Direction.values().size

        private operator fun Direction.plus(turn: Turn): Direction {
            var next = (ordinal + turn.vector) % 4
            if (next < 0) {
                next += numDirections
            }
            return Direction.values()[next]
        }

        private operator fun Direction.minus(other: Direction): Turn {
            var vector = ordinal - other.ordinal
            if (vector < -1) {
                vector += numDirections
            }
            return Turn.values().first { it.vector == vector }
        }
    }
}

val cube1 = Cube(
    size = 4,
    edges = Grid.read("0010\n2340\n0056".lines()) { it, _ -> it.digitToInt() },
    connections = listOf(
        Connection(1, UP, 2, UP),
        Connection(1, LEFT, 3, UP),
        Connection(1, RIGHT, 6, RIGHT),
        Connection(2, UP, 1, UP),
        Connection(2, DOWN, 5, DOWN),
        Connection(2, LEFT, 6, DOWN),
        Connection(3, UP, 1, LEFT),
        Connection(3, DOWN, 5, LEFT),
        Connection(4, RIGHT, 6, UP),
        Connection(5, DOWN, 2, DOWN),
        Connection(5, LEFT, 3, DOWN),
        Connection(6, RIGHT, 1, RIGHT),
        Connection(6, DOWN, 2, LEFT),
        Connection(6, UP, 4, RIGHT),
    ),
)

val cube2 = Cube(
    size = 50,
    edges = Grid.read("012\n030\n450\n600".lines()) { it, _ -> it.digitToInt() },
    connections = listOf(
        Connection(1, LEFT, 4, LEFT),
        Connection(1, UP, 6, LEFT),
        Connection(2, DOWN, 3, RIGHT),
        Connection(2, RIGHT, 5, RIGHT),
        Connection(2, UP, 6, DOWN),
        Connection(3, RIGHT, 2, DOWN),
        Connection(3, LEFT, 4, UP),
        Connection(4, LEFT, 1, LEFT),
        Connection(4, UP, 3, LEFT),
        Connection(5, RIGHT, 2, RIGHT),
        Connection(5, DOWN, 6, RIGHT),
        Connection(6, LEFT, 1, UP),
        Connection(6, DOWN, 2, UP),
        Connection(6, RIGHT, 5, DOWN),
    ),
)
