package serie1.problem

import mergeSort
import java.io.BufferedReader

// Usage: kotlin -Xmx32m <filterFiles> <word1> <word2> <outputFile> <sourceFiles ...>
// Since we are using an IDE, first 2 parameters (-Xmx32, filterFiles) are irrelevant for us.
// We should only care about: 0:<word1> 1:<word2> 2:<outputFile> 3..N:<sourceFiles ...>

fun main(args: Array<String>) {
    wordFilterV2(args)
}

// args -> 0:<word1> 1:<word2> 2:<outputFile> 3..N:<sourceFiles ...>
fun wordFilterV2(args: Array<String>){
    if (args.size < 4) {
        throw Exception("ERROR: Please specify all arguments: <word1> <word2> <output-file> <source-files ...>")
    }

    val (startWord, endWord) = Pair(args[0], args[1])
    if (startWord > endWord) {
        throw Exception("ERROR: <word1> must be lexicographically inferior to <word2>")
    }

    val outputFileName = args[2]
    val inputFilesCount = args.size - 3

    val inputFiles = Array<BufferedReader>(inputFilesCount) { createReader(args[it + 3]) }
    val currentFileLine = MutableList<String>(inputFilesCount) { " " }
    val changed = BooleanArray(inputFilesCount) { true } // true if we need to read next line
    val endOfFile = BooleanArray(inputFilesCount) { false } // true if we reached the end of a file

    val outputFile = createWriter(outputFileName)

    while (!endOfFile.isAll(true)) {
        inputFiles.myForEachIndexed { index, buffReader ->
            if (endOfFile[index] || !changed[index]) {
                return@myForEachIndexed
            }

            var line = buffReader.readLine()
            if (line == null) {
                endOfFile[index] = true
                currentFileLine[index] = ""
                return@myForEachIndexed
            }

            while (line !in startWord..endWord || currentFileLine.contains(line)) {
                line = buffReader.readLine()
                if (line == null) {
                    endOfFile[index] = true
                    currentFileLine[index] = ""
                    return@myForEachIndexed
                }
            }

            currentFileLine[index] = line
        }

        changed.setAll(false)

        val (idx, str) = getMinStr(currentFileLine)
        outputFile.println(str)
        changed[idx] = true
    }

    for (fp in inputFiles) {
        fp.close()
    }
    outputFile.close()
}


