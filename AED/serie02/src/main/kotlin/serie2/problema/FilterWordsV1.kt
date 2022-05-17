package serie2.problema

import serie2.benchmarkFn

fun main(args: Array<String>) {
    filterWordsV1(args)

    // use these to debug:
    //benchmarkFilterWordsV1(arrayOf("carochinha.txt"))
    //benchmarkFilterWordsV1(arrayOf("porquinhos.txt"))
}

fun filterWordsV1(args: Array<String>) {
    if (args.isEmpty())
        throw Exception("File argument null")

    val wordsSet = HashSet<String>()
    wordsSet.addAll(readFileWords(args[0]))

    do {
        println(); print("command: ")
        val fullCommand = readln().split(" ")

        when (fullCommand[0]) {
            "allWords" -> println("Words count: ${wordsSet.size}\n$wordsSet")
            "withDim" -> {
                if (fullCommand.size != 2) {
                    println("withDim k -> Make sure to insert a valid \"k value\". ")
                    continue
                }
                val k = fullCommand[1].toInt()
                println("Words with dimension $k:")
                print("[")
                for (w in wordsSet) {
                    if (w.length == k)
                        print("$w, ")
                }
                print("]")
                println()
            }
            "exit" -> break
            else -> println("Command \"${fullCommand[0]}\" not found!")
        }
    } while (true)
}

fun benchmarkFilterWordsV1(args: Array<String>) {
    if (args.isEmpty())
        throw Exception("File argument null")

    val wordsSet = HashSet<String>()

    var avg = benchmarkFn(1) {
        wordsSet.addAll(readFileWords(args[0]))
    }
    println("V1 reading the entire file (${args[0]}) took $avg ms")

    do {
        println(); print("command: ")
        val fullCommand = readln().split(" ")
        avg = benchmarkFn(1) {
            when (fullCommand[0]) {
                "allWords" -> println("Words count: ${wordsSet.size}\n$wordsSet")
                "withDim" -> {
                    if (fullCommand.size != 2) {
                        println("withDim k -> Make sure to insert a valid \"k value\". ")
                        return@benchmarkFn
                    }
                    val k = fullCommand[1].toInt()
                    println("Words with dimension $k:")
                    print("[")
                    for (w in wordsSet) {
                        if (w.length == k)
                            print("$w, ")
                    }
                    print("]")
                    println()
                }
                "exit" -> return@benchmarkFn
                else -> println("Command \"${fullCommand[0]}\" not found!")
            }
        }
        println("V1 (${args[0]}) \"$fullCommand\" took $avg ms")
    } while (true)
}