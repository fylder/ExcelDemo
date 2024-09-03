package tool

import java.io.File
import java.util.regex.Pattern

class StringXmlTool {

    fun start() {
        startReserve()
//        startChinese()
    }

    //原始列表过滤新增的字段列表
    private fun startReserve() {
        val destList = readXml(name = "strings_language.xml")
        val srcList = readXml(name = "strings.xml")
        println("destStr size: ${destList.size}")
        println("srcStr size: ${srcList.size}")

        //获取相同name
        val shareItem = arrayListOf<ItemBean>()
        destList.forEach {
            srcList.find { srcItem ->
                srcItem.name == it.name
            }?.let { result ->
//                println("find result: $result")
                shareItem.add(result)
            }
        }
        println("shareItem size: ${shareItem.size}")
        shareItem.forEach {
            if (it.name.contains("next")) {
                println("过滤查询的 item: $it")
            }
        }

        //移除原始列表相同数据
        println("移除前srcList size: ${srcList.size}")
        shareItem.forEach {
            srcList.removeIf { removeItem ->
                removeItem.name == it.name
            }
        }
        println("移除后srcList size: ${srcList.size}")

        srcList.forEach {
            if (it.name.contains("next")) {
                println("过滤查询的 srcList item: $it")
            }
        }

//        //原始列表过滤新增的字段列表
//        srcList.forEach {
//            println("find result: $it")
//        }

        writeXml(srcList)
    }

    //提取相同字段中文
    private fun startChinese() {
        val srcZhList = readXml(name = "strings_src_zh.xml")
        val srcEnList = readXml(name = "strings_src_en.xml")
        println("srcZhList size: ${srcZhList.size}")
        println("srcEnList size: ${srcEnList.size}")

        //获取相同name
        val newItem = arrayListOf<ItemBean>()
        srcEnList.forEach {
            srcZhList.find { item ->
                item.name == it.name
            }?.let { result ->
//                println("find result: $result")
                newItem.add(result)
            }
        }
        println("newItem size: ${newItem.size}")

        writeXml(newItem, name = "strings_zh_out.xml")
    }

    private fun readXml(name: String = "strings_language.xml", dir: String = "./resources/topvci/test"): ArrayList<ItemBean> {
        val data = arrayListOf<ItemBean>()
        File(dir, name)
            .readLines().let {
                it.forEach { line ->
                    xml(line)?.let { bean ->
                        data.add(bean)
                    }
                }
            }
        return data
    }

    private fun writeXml(items: ArrayList<ItemBean>, name: String = "strings_out.xml", dir: String = "./resources/topvci/test") {
//        val format = "<string name=\"%s\">%s</string>"
//        File(dir, name)
//            .let { file ->
//                items.forEach {
//                    val str = String.format(format, it.name, it.value)
//                    file.writeText(str)
//                }
//            }

        val format = "<string name=\"%s\">%s</string>"
        val template = StringBuilder()
        template.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append("\n")
        template.append("<resources>").append("\n")
        items.forEach {
            val str = String.format(format, it.name, it.value)
            template.append("\t").append(str).append("\n")
        }
        template.append("</resources>").append("\n")
        File(dir, name).writeText(template.toString())

    }

    private fun xml(str: String = "<string name=\"gusha\">yada</string>"): ItemBean? {
        val pattern = Pattern.compile("<string name=\"(.*)\">(.*)</string>")
        val matcher = pattern.matcher(str)
        return if (matcher.find()) {
            for (i in 0 until matcher.groupCount()) {
//                println("group $i: ${matcher.group(i + 1)}")
            }
            ItemBean(name = matcher.group(1), value = matcher.group(2))
        } else {
            null
        }
    }

    data class ItemBean(val name: String, val value: String)
}