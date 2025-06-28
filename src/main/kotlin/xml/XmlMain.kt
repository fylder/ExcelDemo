package xml

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * xml转excel
 *
 * 需要引入的库
 * implementation 'org.apache.poi:poi-ooxml:3.17'
 * implementation 'org.apache.xmlbeans:xmlbeans:3.1.0'
 *
 */
fun main() {
    println("start")
    val file = File("./resources/xml/strings_zh_2025-06-28.xml")
    val lines = file.readLines()
    val keyName = arrayListOf<XMLBean>()

    readLine(lines, keyName)
    keyName.sortWith { b1, b2 -> b1.key.compareTo(b2.key) }

    exportExcel(listData = keyName, fileDir = "./resources/xml/output", fileName = "ahh_${getNowDate()}.xlsx")
    println("finish")
}

fun getKey(str: String): String {
    val startFlag = "<string name=\""
    val endFlag = "\">"
    val indexStart = str.indexOf(startFlag) + startFlag.length
    val indexEnd = str.indexOf(endFlag)
    return str.substring(indexStart, indexEnd)
}

fun getName(str: String): String {
    val startFlag = "\">"
    val endFlag = "</string>"
    val indexStart = str.indexOf(startFlag) + startFlag.length
    val indexEnd = str.indexOf(endFlag)
    return str.substring(indexStart, indexEnd)
}

//读取xml内容
fun readLine(lines: List<String>, keyName: ArrayList<XMLBean>) {
    val strBuilder = StringBuilder()
    var readItemFinish = false
    lines.forEach {
        if (it.contains("<string name") && it.contains("</string>")) {
            //完整一行
            strBuilder.append(it)
            readItemFinish = true
        } else if (it.contains("<string name") && !it.contains("</string>")) {
            //多行开始
            strBuilder.append(it)
        } else if (!it.contains("<string name") && it.contains("</string>")) {
            //多行结尾
            strBuilder.append("\n").append(it)
            readItemFinish = true
        } else {
            //中间部分
            if (strBuilder.isNotEmpty()) {
                //继续追加内容
                strBuilder.append("\n").append(it)
            } else {
                if (it.isNotBlank()) {
                    println("无效内容: $it")
                }
            }
        }
        if (readItemFinish) {
            val str = strBuilder.toString()
            val key = getKey(str)
            val name = getName(str)
            keyName.add(XMLBean(key = key, name = name))
            strBuilder.clear()
            readItemFinish = false
        }
    }
}


/**
 * 导出Excel
 * @param listData
 * @return
 */
fun exportExcel(listData: List<XMLBean>, fileDir: String = "./xml", fileName: String = "ahh.xlsx"): Boolean {
    return try {
        // 创建excel xlsx格式
        val wb: Workbook = XSSFWorkbook()
        // 创建工作表
        val sheet: Sheet = wb.createSheet()
        val title = arrayOf("key", "内容", "备注")
        // 创建行对象
        var row: Row = sheet.createRow(0)
        // 设置有效数据的行数和列数
        val colNum = title.size // {"用户", "寄"}
        for (i in 0 until colNum) {
            sheet.setColumnWidth(i, 20 * 256) // 显示20个字符的宽度
            val cell1: Cell = row.createCell(i)
            //第一行
            cell1.setCellValue(title[i])
        }

        // 导入数据
        for (rowNum in listData.indices) {
            // 之所以rowNum + 1 是因为要设置第二行单元格
            row = sheet.createRow(rowNum + 1)
            // 设置单元格显示宽度
            row.heightInPoints = 28f
            val bean: XMLBean = listData[rowNum]
            for (j in title.indices) {
                val cell: Cell = row.createCell(j)
                when (j) {
                    0 -> cell.setCellValue(bean.key) //用户
                    1 -> cell.setCellValue(bean.name) //寄件人姓名
                }
            }
        }
        // 目录
        val dir = File(fileDir)
        //判断文件是否存在
        if (!dir.isFile) {
            //不存在则创建
            dir.mkdir()
        }
        val excel = File(dir, fileName)
        val fos = FileOutputStream(excel)
        wb.write(fos)
        fos.flush()
        fos.close()
        println("excel的导出文件: ${excel.path}")
        true
    } catch (e: IOException) {
        println("Express Excel: ${e.message}")
        false
    }
}

fun getNowDate(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = Date(System.currentTimeMillis())
    return formatter.format(date)
}

data class XMLBean(val key: String, val name: String)