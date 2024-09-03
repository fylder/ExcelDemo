package tool

import excel.ExcelDemo
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

object ExcelTool {

    var isSuccess = true

    /**
     * 读取xlsx
     */
    fun readExcel(path: String, sheetName: String): ArrayList<KeyName> {
        val file = File(path)
        val wb = XSSFWorkbook(file)
        val sheet = wb.getSheet(sheetName)
        if (sheet == null) {
            println("sheet无效")
            return arrayListOf()
        }
        var skipIndex = 0
        sheet.getRow(0).forEach {
            if (it.stringCellValue == "原始英文") {
                skipIndex = it.columnIndex
            }
        }
        // 遍历sheet所有行
        val dataList = arrayListOf<KeyName>()
        val itemSize = sheet.getRow(0).lastCellNum //读取首行个数
        Log.println("检索行数: ${sheet.lastRowNum}", isPrint = false)
        for (row in 0..sheet.lastRowNum) {
            val row1 = sheet.getRow(row) ?: continue //获取行
            val bean = KeyName()

            // 判断首个空字符跳过(android_id不能为空)
            row1.getCell(0) ?: continue
            row1.getCell(0).rawValue ?: continue

            // 根据首行数量遍历所有对应列内容
            for (column in 0 until itemSize) {
                val cellValue = row1.getCell(column)
//                println("${cellValue?.rawValue} value: ${cellValue?.stringCellValue}")
//                cellValue?.rawValue ?: continue
                //单元格空白的值有可能是[null, ""]
                if (cellValue != null) {
                    val str = cellValue.stringCellValue
                    if (column == 0) {
                        bean.key = str //读取key
                    } else if (column == 2) {
                        bean.name = str //读取中文文本
                    } else if (column > 2 && column != skipIndex) {
                        if (str.isNullOrBlank()) {
                            Log.println("请补充翻译 ${errorStr(sheet, row, column, "\"\"")}")
                        }
                        bean.items = bean.items.plus(str)
                    }
                } else {
                    if (column != 1 && column != 3) {
                        //备注和原始英文不提示
                        Log.println("请补充翻译 ${errorStr(sheet, row, column, "null")}")
                        bean.items = bean.items.plus("")
                    }
                }
            }
            if (bean.key.isNotBlank()) {
                bean.index = dataList.size
                dataList.add(bean)
            } else {
                println("[${row + 1}] bean.key.isNotBlank()")
            }
            if (row == 0) {
                Log.println("检索标题: ${dataList[0]}", isPrint = false)
            } else if (row == 1) {
                Log.println("检索内容: ${dataList[1]}", isPrint = false)
            }
        }
        return dataList
    }

    /**
     * 写入strings.xml
     *
     * @param beans     表格内容键值对
     * @param outDir    输出目录
     * @param index     0为中文    大于0为其它语言序号
     * @param filterKeys  过滤写入的key
     */
    fun writeXml(
        beans: ArrayList<KeyName>,
        outDir: String = "./resources/ahh",
        outName: String = "strings_language.xml",
        index: Int = -1,
        filterKeys: ArrayList<String>,
        lanType: ExcelDemo.XmlType = ExcelDemo.XmlType(type = "zh", typeStr = "zh"),
    ): Boolean {
        var currentItem: KeyName? = null
        var currentName = ""
        var currentOutDir = outDir
        if (ExcelDemo.XML_VALUE_EMPTY) {
            currentOutDir = currentOutDir.replace("/res/", "/res_filter_empty/")
        }
        try {
            val template = StringBuilder()
            template.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\n")
            template.append("<resources>").append("\n")
            template.append("%s")
            template.append("</resources>").append("\n")
            val values = StringBuilder()
            beans.forEach {
                if (filterKeys.contains(it.key.lowercase())) {
                    return@forEach //返回继续下一个
                }
                currentItem = it
                var nameStr = if (index >= 0) it.items[index] else it.name //index为0时中文写入,用[name],其它语言用[items[index]]
                currentName = nameStr
                // 矫正字符
                nameStr = strReplace(nameStr)
                val str: String = "<string name=\"" + it.key + "\">" + nameStr + "</string>"
                if (ExcelDemo.XML_VALUE_EMPTY) {
                    //方案一 值为空也写入
                    values.append("\t").append(str).append("\n")
                } else {
                    //方案二 值为空不写入
                    if (nameStr.isNotBlank()) {
                        values.append("\t").append(str).append("\n")
                    } else {
                        if (lanType.type == "en") {
                            //默认英文为空,用中文代替,必须保证默认有值
                            println("默认英文为空,用中文代替,请及时翻译 $it")
                            var cnNameStr = it.name
                            // 矫正字符
                            cnNameStr = strReplace(cnNameStr)
                            val cnStr: String = "<string name=\"" + it.key + "\">" + cnNameStr + "</string>"
                            values.append("\t").append(cnStr).append("\n")
                        }
                    }
                }
            }
            val content = String.format(template.toString(), values.toString())
            val outFile = File(currentOutDir, outName)
            File(currentOutDir).let {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }
            outFile.writeText(content)
            return true
        } catch (e: Exception) {
            isSuccess = false
            System.err.println("写入异常 上一个读取: $currentName, currentItem: $currentItem")
            e.printStackTrace()
            return false
        }
    }

    //矫正
    private fun strReplace(str: String): String {
        var result = str
        result = result.replace(" ", " ")
        result = result.replace("'", "\\'").replace("\\\\'", "\\'")
        result = result.replace("%@", "%s")
        result = result.replace("<", "&lt;")
        return result
    }

    //单元格序号
    private fun cellStr(row: Int, column: Int): String {
        val cellStr = StringBuilder()
        cellStr.append("[${row + 1}, ${column + 1}]")
        if (column / 26 < 1) {
            val str: Char = (column % 26).toChar() + 'A'.code
            cellStr.append(" - ${str}${row + 1}")
        } else {
            val firstChar = (column / 26).toChar() + 'A'.code - 1
            val str: Char = (column % 26).toChar() + 'A'.code
            cellStr.append(" - ${firstChar}${str}${row + 1}")
        }
        return cellStr.toString()
    }

    private fun errorStr(sheet: XSSFSheet, row: Int, column: Int, param: String): String {
        val cellTitle = sheet.getRow(0).getCell(column).stringCellValue //标题
        val cellId = sheet.getRow(row).getCell(0).stringCellValue //id
        return "${cellStr(row, column)}, \"${cellId}\" = $param, $cellTitle"
    }

}

data class KeyName(
    var index: Int = 0, var key: String = "", var name: String = "", var items: Array<String> = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyName

        if (index != other.index) return false
        if (key != other.key) return false
        if (name != other.name) return false
        if (!items.contentEquals(other.items)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + key.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + items.contentHashCode()
        return result
    }
}