package aoc2022

import aoc2022.Day7.Directory

/**
 * [Day 7: No Space Left On Device](https://adventofcode.com/2022/day/7)
 */
object Day7 : Puzzle<Directory, Long> {

    override val name = "🔠🔢🔣 No Space Left On Device"

    private const val TOTAL_DISK_SPACE = 70_000_000
    private const val MINIMAL_UNUSED_SPACE = 30_000_000

    override fun parseInput(lines: List<String>): Directory =
        Directory("/").also { root ->
            lines.fold(root) { pwd, command ->
                val tokens = command.split(" ")
                when {
                    tokens[0] == "$" && tokens[1] == "ls" -> pwd
                    tokens[0] == "$" && tokens[1] == "cd" -> when (tokens[2]) {
                        "/" -> root
                        ".." -> pwd.parent!!
                        else -> pwd.children.first { it.name == tokens[2] } as Directory
                    }
                    else -> pwd.apply {
                        children += if (tokens[0] == "dir") {
                            Directory(name = tokens[1], parent = pwd)
                        } else {
                            File(name = tokens[1], parent = pwd, size = tokens[0].toLong().also { pwd.grow(it) })
                        }
                    }
                }
            }
        }

    /**
     * What is the sum of the total sizes of those directories with a total size of at most 100000?
     */
    override fun part1(input: Directory): Long = input.totalSizes().filter { it <= 100_000 }.sum()

    /**
     * What is the total size of the smallest directory needed to delete to free up enough space?
     */
    override fun part2(input: Directory): Long {
        val needToFree = MINIMAL_UNUSED_SPACE - (TOTAL_DISK_SPACE - input.totalSize)
        return input.totalSizes().sorted().first { it >= needToFree }
    }

    private fun Directory.totalSizes(): List<Long> =
        listOf(totalSize) + children.filterIsInstance<Directory>().flatMap { it.totalSizes() }

    sealed interface Node {
        val name: String
        val parent: Directory?
    }

    class Directory(
        override val name: String,
        override val parent: Directory? = null,
        val children: MutableList<Node> = mutableListOf(),
        var totalSize: Long = 0,
    ) : Node {

        fun grow(size: Long) {
            totalSize += size
            parent?.grow(size)
        }

        override fun toString(): String = "dir $name"
    }

    class File(
        override val name: String,
        override val parent: Directory,
        val size: Long,
    ) : Node {
        override fun toString(): String = "$name ($size)"
    }
}
