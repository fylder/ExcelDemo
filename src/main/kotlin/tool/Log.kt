package tool

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Log {

    private const val dir = "./log"

    private val logName: String
        get() {
            val today = time("yyyy-MM-dd")
            return "${dir}/${today}.log"
        }

    private fun initDir() {
        File(dir).let {
            if (!it.isDirectory) {
                it.mkdirs()
            }
        }
    }

    fun clear() {
        initDir()
        File(dir).listFiles()?.forEach {
            it.delete()
        }
    }

    fun println(msg: String, isPrint: Boolean = false) {
        if (isPrint) {
            kotlin.io.println(msg)
        }
        initDir()
        val strBuilder = StringBuilder(time())
        strBuilder.append(" $msg")
        strBuilder.append("\n")
        File(logName).let { file ->
            FileOutputStream(file, true).bufferedWriter().use {
                it.append(strBuilder)
            }
        }

    }

    private fun time(pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): String {
        val dateFormat = SimpleDateFormat(pattern)
        return dateFormat.format(Date(System.currentTimeMillis()))
    }
}