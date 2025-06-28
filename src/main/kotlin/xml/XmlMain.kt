package xml

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.w3c.dom.Element
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

/**
 * xml转excel
 *
 * 需要引入的库
 * implementation 'org.apache.poi:poi-ooxml:3.17'
 * implementation 'org.apache.xmlbeans:xmlbeans:3.1.0'
 *
 */

private const val hasSort = false //是否对key排序

fun main() {
    println("start")
    val file = File("./resources/xml/strings.xml")
    val keyName = arrayListOf<XMLBean>()
    parseStringsXml(file).forEach {
        keyName.add(XMLBean(key = it.key, name = it.value))
    }
    if (hasSort) {
        keyName.sortWith { b1, b2 -> b1.key.compareTo(b2.key) }
    }
    exportExcel(listData = keyName, fileDir = "./resources/xml/output", fileName = "ahh_${getNowDate()}.xlsx")
    println("finish")
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
        val title = arrayOf("key", "内容")
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

//读取xml内容
fun parseStringsXml(file: File): Map<String, String> {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document = builder.parse(file)

    val resources = document.documentElement
    val stringNodes = resources.getElementsByTagName("string")

    return (0 until stringNodes.length).associate { i ->
        val node = stringNodes.item(i) as Element
        val name = node.getAttribute("name")
        val value = node.textContent
        println("解析内容   ${name}: ${value}")
        name to value
    }
}

fun getNowDate(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = Date(System.currentTimeMillis())
    return formatter.format(date)
}

data class XMLBean(val key: String, val name: String)