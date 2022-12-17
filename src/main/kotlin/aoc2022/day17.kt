package aoc2022

import aoc2022.Day17.Terrain.*
import aoc2022.Direction.*
import kotlin.math.max

/**
 * [Day 17: Pyroclastic Flow](https://adventofcode.com/2022/day/17)
 */
object Day17 : Puzzle<List<Direction>, Long> {

    override val name = "🐘🕹🧱 Pyroclastic Flow"

    override fun parseInput(lines: List<String>): List<Direction> =
        lines.first().map {
            when (it) {
                '<' -> LEFT
                '>' -> RIGHT
                else -> error("Unknown symbol: '$it'")
            }
        }

    private const val CHAMBER_WIDTH = 7
    private const val ROCK_START_X = 2
    private const val ROCK_START_Y_OFFSET = 3
    private const val KEY_ROWS = 5

    /**
     * How many units tall will the tower of rocks be after 2022 rocks have stopped falling?
     */
    override fun part1(input: List<Direction>): Long = towerHeight(input, 2022)

    /**
     * How tall will the tower be after 1 000 000 000 000 rocks have stopped?
     */
    override fun part2(input: List<Direction>): Long = towerHeight(input, 1_000_000_000_000)

    private fun towerHeight(input: List<Direction>, rocks: Long): Long {
        val shapes = Shape.values()
        val chamber = Grid.from(1, CHAMBER_WIDTH) { ROCK }
        var towerHeight = 0
        var shapeIndex = 0
        var jetIndex = 0
        var counter = 0L
        var cycleDetected = false
        var skippedHeight = 0L
        val keys = mutableSetOf<Key>()
        while (counter++ < rocks) {
            if (!cycleDetected && towerHeight > KEY_ROWS) {
                val row = chamber
                    .slice(towerHeight - KEY_ROWS..towerHeight, 0 until CHAMBER_WIDTH)
                    .joinToString("") { "${it.value.char}" }
                val key = Key(counter, towerHeight, shapeIndex, jetIndex, row)
                if (!keys.add(key)) {
                    cycleDetected = true
                    val previous: Key = keys.first { it == key }
                    val rocksLeft = rocks - counter
                    val rocksInCycle = key.counter - previous.counter
                    val cycleHeight = key.towerHeight - previous.towerHeight
                    val skipCycles = rocksLeft / rocksInCycle - 10
                    skippedHeight = cycleHeight * skipCycles
                    counter += rocksInCycle * skipCycles
                }
            }
            val shape = shapes[shapeIndex++]
            shapeIndex %= shapes.size
            val rockStartY = towerHeight + ROCK_START_Y_OFFSET + 1
            val rock = Rock(shape, bottomLeft = Point(ROCK_START_X, rockStartY))
            if (chamber.rows <= rock.topRight.y) {
                chamber.expand(rock.topRight.y - chamber.rows + 1) { AIR }
            }
            //print("Rock #$counter = $shape", chamber, rock)
            do {
                val jet = input[jetIndex++]
                jetIndex %= input.size
                rock.tryMove(jet, chamber)
            } while (rock.tryMove(DOWN, chamber))
            rock.stop(chamber)
            //print("STOP", chamber)
            towerHeight = max(towerHeight, rock.topRight.y)
        }
        //print("FINISH", chamber)
        return skippedHeight + towerHeight
    }

    data class Key(
        val counter: Long,
        val towerHeight: Int,
        val shapeIndex: Int,
        val jetIndex: Int,
        val row: String,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Key

            if (shapeIndex != other.shapeIndex) return false
            if (jetIndex != other.jetIndex) return false
            if (row != other.row) return false

            return true
        }

        override fun hashCode(): Int {
            var result = shapeIndex
            result = 31 * result + jetIndex
            result = 31 * result + row.hashCode()
            return result
        }
    }

    data class Rock(val shape: Shape, var bottomLeft: Point) {

        val topRight: Point get() = Point(bottomLeft.x + shape.grid.cols - 1, bottomLeft.y + shape.grid.rows - 1)

        fun tryMove(jet: Direction, chamber: Grid<Terrain>): Boolean =
            canMove(jet, chamber).also { can ->
                if (can) {
                    bottomLeft += jet.point
                    //print(jet.name, chamber, this)
                }
            }

        fun stop(chamber: Grid<Terrain>) {
            shape.grid.filter { it.value == MOVING }.forEach {
                chamber[it.point + bottomLeft] = ROCK
            }
        }

        private fun canMove(direction: Direction, chamber: Grid<Terrain>): Boolean =
            shape.grid.filter { it.value != AIR }.all {
                val chamberPoint = bottomLeft + it.point + direction.point
                chamberPoint in chamber && chamber[chamberPoint].value == AIR
            }
    }

    enum class Shape(shape: String) {
        HORIZONTAL("@@@@"),
        CROSS(".@.\n@@@\n.@."),
        ANGLE("@@@\n..@\n..@"),
        VERTICAL("@\n@\n@\n@"),
        SQUARE("@@\n@@");

        val grid: Grid<Terrain> = Grid.from(shape.trimIndent().lines()) { char, _ -> Terrain.from(char) }
    }

    enum class Terrain(val char: Char) {

        AIR('.'),
        ROCK('#'),
        MOVING('@');

        companion object {
            fun from(char: Char): Terrain = values().first { it.char == char }
        }
    }

    private fun print(header: String, chamber: Grid<Terrain>, rock: Rock? = null) {
        println(header)
        println()
        chamber.print { cell ->
            when {
                cell.point.y == 0 -> '-'

                rock != null
                        && cell.point.x in rock.bottomLeft.x..rock.topRight.x
                        && cell.point.y in rock.bottomLeft.y..rock.topRight.y -> {
                    val shapePoint = cell.point - rock.bottomLeft
                    val terrain = rock.shape.grid[shapePoint].value
                    terrain.takeUnless { it == AIR }?.char ?: cell.value.char
                }

                else -> cell.value.char
            }
        }
        println()
    }
}
