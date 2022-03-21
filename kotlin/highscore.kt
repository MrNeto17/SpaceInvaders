package game

import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


val path = "C:/Users/Public/Desktop"
fun takescore(file: String): Int{
    FileOutputStream("$path$file", true)
    val emptyornot: BufferedReader = File("$path$file").bufferedReader()
    if (emptyornot.use { it.readText() } == "") storescore(file, 0)
    val bufferfile: BufferedReader = File("$path$file").bufferedReader()
    return bufferfile.use { it.readText() }.toInt()
}
fun storescore(file: String, i:Int){
    File("$path$file").printWriter().use { out ->
        out.print(i)
    }
}
