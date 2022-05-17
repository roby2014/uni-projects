package serie2.problema

import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter

fun createReader(fileName: String): BufferedReader {
    return BufferedReader(FileReader(fileName))
}

fun createWriter(fileName: String?): PrintWriter {
    return PrintWriter(fileName)
}

fun BufferedReader.readFileLines(block: (String) -> Unit) {
    var line = readLine()
    while (line != null) {
        block(line)
        line = readLine()
    }
}

/** Usage Example
 *  File on the project Directory:
 *  Copy Input File to OutputFile.
 * **/
fun main( ){
    val br= createReader("input.txt")
    val pw= createWriter("output.txt")
    var line:String?
    line=br.readLine()
    while(line!=null){
        pw.println(line)
        line=br.readLine()
    }
    pw.close()
}