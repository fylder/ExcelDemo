package xml

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
//    val file = File("./resources/xml/strings.xml")
    val file = File("./resources/xml/strings_zh_2023-12-15.xml")
    val lines = file.readLines()
    val keyName = arrayListOf<XMLBean>()
    lines.forEach {
        if (it.contains("<string name")) {
            val key = getKey(it)
            val name = getName(it)
            keyName.add(XMLBean(key = key, name = name))
        }
    }
    keyName.sortWith { b1, b2 -> b1.key.compareTo(b2.key) }

    exportExcel(listData = keyName, fileDir = "./resources/xml", fileName = "ahh_${System.currentTimeMillis()}_.xlsx")
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
        val title = arrayOf("key", "ZH", "备注")
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
        true
    } catch (e: IOException) {
        println("Express Excel: ${e.message}")
        false
    }
}


data class XMLBean(val key: String, val name: String)