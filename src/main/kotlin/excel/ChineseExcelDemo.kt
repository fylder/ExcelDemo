package excel

import bean.XmlType
import tool.ExcelTool
import tool.KeyName

/**
 * 导出strings.xml
 */
class ChineseExcelDemo {

    private val project = "topvci" //项目名
    private val xlsxName = "topvci_2024-01-20.xlsx" //读取文件
    private val resDir = "./resources" //资源根目录
    private val sheetName = "Sheet0" //表格

    private val keyName = arrayListOf("android_id", "id")  //该行不写入文件
    private var isSuccess = true

    fun startTask() {
        val filePath = "${resDir}/${project}/${xlsxName}"
        println("解析${filePath}")
        val bean = ExcelTool.readExcel(path = filePath, sheetName = sheetName)
        writeMoreXml(bean)
        if (isSuccess) {
            println("导出结束")
        } else {
            System.err.println("导出结束,写入失败")
        }
    }

    // ["校正英文", "繁体中文", "日语", "俄语", "德语", "西班牙语", "葡萄牙语", "法语", "意大利语", "波兰语", "荷兰"]
    // typeStr与表格上的文本模糊匹配
    private fun writeMoreXml(beans: ArrayList<KeyName>) {

        val types: ArrayList<XmlType> = arrayListOf()
        types.add(XmlType(type = "zh", typeStr = "zh")) //中文特殊类型,不需要修改
//        types.add(XmlType(type = "en", typeStr = "校正英文"))
//        types.add(XmlType(type = "zh-hk", typeStr = "繁体中文"))

        // 中文
        ExcelTool.writeXml(beans, outDir = "${resDir}/${project}/res/values-zh", filterKeys = keyName)

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
                ExcelTool.writeXml(beans, outDir = "${resDir}/${project}/res/values-${it.type}", index = index, filterKeys = keyName)
            }
        }
    }

}