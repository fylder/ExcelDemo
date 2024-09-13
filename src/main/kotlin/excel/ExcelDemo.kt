package excel

import bean.XmlType
import config.LanConfig
import tool.ExcelTool
import tool.KeyName
import java.io.File

/**
 * 导出strings.xml
 */
class ExcelDemo {

    companion object {
        const val XML_VALUE_EMPTY = false //输出的string.xml字段是否允许空内容
    }

//    private val project = "js2000" //项目名
//    private val xmlName = "JS2000_2022-12-19.xlsx" //读取文件
//    private val project = "tc001" //项目名
//    private val xmlName = "tc001_2023-01-12.xlsx" //读取文件
//    private val project = "bt20" //项目名
//    private val xmlName = "BT20_2023-01-31.xlsx" //读取文件

    private val project = "carpal" //项目名
    private val xlsxName = "carpal_2024-09-12.xlsx" //读取文件
    private val resDir = "./resources" //资源根目录
    private val sheetName = "Sheet0" //表格名称
    private val filterKeyName = arrayListOf("android_id", "id")  //该行不写入文件

    fun startTask() {
        val filePath = "${resDir}/${project}/${xlsxName}"
        println("解析${filePath}")
        clearRes()
        val bean = ExcelTool.readExcel(path = filePath, sheetName = sheetName)
        writeMoreXml(bean)
        if (ExcelTool.isSuccess) {
            println("导出结束")
        } else {
            System.err.println("导出结束,写入失败")
        }
    }

    // ["校正英文", "繁体中文", "日语", "俄语", "德语", "西班牙语", "葡萄牙语", "法语", "意大利语", "波兰语", "荷兰"]
    // typeStr与表格上的文本模糊匹配
    private fun writeMoreXml(beans: ArrayList<KeyName>) {

        //定义语言列表
        val types: ArrayList<XmlType> = LanConfig.typeList(project)

        val valueDir = "${resDir}/${project}/res/values"
        val fileName = "strings.xml"

        // 中文
        ExcelTool.writeXml(beans, outDir = "${valueDir}-${types[0].type}", outName = fileName, filterKeys = filterKeyName, lanType = types[0])

        // 其它语言 - 检索表格上到的语言
        for (index in 0 until beans[0].items.size) {
            var xmlType: XmlType? = null
            run stop@{
                types.forEach {
                    if (beans[0].items[index].contains(it.typeStr)) {
                        xmlType = it
                        return@stop
                    }
                }
            }
            xmlType?.let {
                var outDir = "${valueDir}-${it.type}"
                if (it.type == LanConfig.DEFAULT_LAN) {
                    outDir = valueDir
                }
                ExcelTool.writeXml(beans, outDir = outDir, outName = fileName, index = index, filterKeys = filterKeyName, lanType = it)

            }
        }
    }

    private fun clearRes() {
        val dir = "${resDir}/${project}"
        File(dir).listFiles()?.filter { it.isDirectory }?.forEach {
            if (it.name.contains("res")) {
                println("删除目录: ${it.path}")
                deleteDirectory(it)
                it.deleteOnExit()
            }
        }
    }

    //递归删除目录下的所有文件和文件夹
    private fun deleteDirectory(directory: File) {
        for (file in directory.listFiles()!!) {
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete() //删除文件
            }
        }
        directory.deleteOnExit() //删除文件夹
    }

}