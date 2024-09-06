package excel

import tool.ExcelTool
import tool.KeyName

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
    private val xmlName = "carpal_2024-09-02.xlsx" //读取文件
    private val resDir = "./resources" //资源根目录
    private val sheetName = "Sheet0" //表格名称

    private val filterKeyName = arrayListOf("android_id", "id")  //该行不写入文件

    fun startTask() {
        val filePath = "${resDir}/${project}/${xmlName}"
        println("解析${filePath}")
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

        val types: ArrayList<XmlType> = arrayListOf()
        types.add(XmlType(type = "zh", typeStr = "zh")) //中文特殊类型,不需要修改
        types.add(XmlType(type = "en", typeStr = "校正英文"))
        types.add(XmlType(type = "zh-hk", typeStr = "繁体中文"))
        types.add(XmlType(type = "jp", typeStr = "日语"))
        types.add(XmlType(type = "ru", typeStr = "俄语"))
        types.add(XmlType(type = "de", typeStr = "德语"))
        types.add(XmlType(type = "es", typeStr = "西班牙语"))
        types.add(XmlType(type = "pt", typeStr = "葡萄牙语"))
        types.add(XmlType(type = "fr", typeStr = "法语"))
        types.add(XmlType(type = "it", typeStr = "意大利语"))
        types.add(XmlType(type = "pl", typeStr = "波兰语"))
        types.add(XmlType(type = "cs", typeStr = "捷克语"))
        types.add(XmlType(type = "uk", typeStr = "乌克兰语"))
        types.add(XmlType(type = "nl", typeStr = "荷兰语"))
        types.add(XmlType(type = "ko", typeStr = "韩语"))
        types.add(XmlType(type = "cs", typeStr = "捷克"))
        types.add(XmlType(type = "tr", typeStr = "土耳其"))
        types.add(XmlType(type = "da", typeStr = "丹麦"))
        types.add(XmlType(type = "no", typeStr = "挪威"))
        types.add(XmlType(type = "sv", typeStr = "瑞典"))
        types.add(XmlType(type = "ar", typeStr = "阿拉伯"))
        types.add(XmlType(type = "sk", typeStr = "斯洛伐克"))
        types.add(XmlType(type = "fi", typeStr = "芬兰"))
        types.add(XmlType(type = "sr", typeStr = "塞尔维亚"))
        types.add(XmlType(type = "hr", typeStr = "克罗地亚"))


        // 中文
        ExcelTool.writeXml(beans, outDir = "${resDir}/${project}/res/values-zh", filterKeys = filterKeyName)

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
                println("写入: value-${it.type}, index: $index")
                ExcelTool.writeXml(beans, outDir = "${resDir}/${project}/res/values-${it.type}", index = index, filterKeys = filterKeyName, lanType = it)
            }
        }
    }

    data class XmlType(val type: String, val typeStr: String)
}