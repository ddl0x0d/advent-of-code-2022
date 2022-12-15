package aoc2022

import aoc2022.Day15.Reading
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * [Day 15: Beacon Exclusion Zone](https://adventofcode.com/2022/day/15)
 */
object Day15 : Puzzle<List<Reading>, Long> {

    override val name = "📡📡📡 Beacon Exclusion Zone"

    private const val PART_1_PARAM: Int = 10
    private const val PART_2_PARAM: Int = 20

    private const val INT = "(-?\\d+)"
    private val pattern = "Sensor at x=$INT, y=$INT: closest beacon is at x=$INT, y=$INT".toRegex()

    override fun parseInput(lines: List<String>): List<Reading> =
        lines.map { line ->
            val (s1, s2, b1, b2) = pattern.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
            Reading(sensor = Point(s1, s2), beacon = Point(b1, b2))
        }

    /**
     * Consult the report from the sensors you just deployed. In the row where y=20, how many positions cannot contain a beacon?
     */
    override fun part1(input: List<Reading>): Long =
        rowCoverage(PART_1_PARAM, input).sumOf { it.last - it.first }.toLong()

    /**
     * What is distress beacon tuning frequency?
     */
    override fun part2(input: List<Reading>): Long =
        (0 until PART_2_PARAM).asSequence()
            .map { row -> row to rowCoverage(row, input) }
            .first { (_, coverage) -> coverage.size > 1 }
            .let { (y, coverage) ->
                val x = coverage.first().last + 1
                4_000_000L * x + y
            }

    private fun rowCoverage(row: Int, readings: List<Reading>): List<IntRange> =
        readings.mapNotNull { (sensor, beacon) ->
            val (vx, vy) = beacon - sensor
            val sensorRadius = vx.absoluteValue + vy.absoluteValue
            val sensorRowsCovered = (sensor.y - sensorRadius)..(sensor.y + sensorRadius)
            if (row in sensorRowsCovered) {
                val sensorToRowDistance = abs(row - sensor.y)
                val rowRadius = sensorRadius - sensorToRowDistance
                (sensor.x - rowRadius)..(sensor.x + rowRadius)
            } else {
                null
            }
        }.sortedBy { it.first }.merge()

    private fun List<IntRange>.merge(): List<IntRange> =
        if (isEmpty()) {
            this
        } else {
            fold(ArrayDeque(take(1))) { merge, next ->
                merge.apply {
                    add(
                        if (last() intersects next) {
                            removeLast() union next
                        } else {
                            next
                        }
                    )
                }
            }
        }

    data class Reading(
        val sensor: Point,
        val beacon: Point,
    )
}
