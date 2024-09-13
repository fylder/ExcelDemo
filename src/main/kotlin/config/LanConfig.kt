package config

import bean.XmlType

object LanConfig {

    //默认语言
    const val DEFAULT_LAN = "en"

    /**
     * 第一项是中文
     */
    fun typeList(project: String): ArrayList<XmlType> {
        return if (project == "carpal") {
            carpalConfig
        } else {
            defaultConfig
        }
    }

    private val defaultConfig: ArrayList<XmlType>
        get() {
            val types: ArrayList<XmlType> = arrayListOf()
            types.add(XmlType(type = "zh", typeStr = "zh"))
            types.add(XmlType(type = DEFAULT_LAN, typeStr = "校正英文"))
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
            types.add(XmlType(type = "tr", typeStr = "土耳其"))
            types.add(XmlType(type = "da", typeStr = "丹麦"))
            types.add(XmlType(type = "no", typeStr = "挪威"))
            types.add(XmlType(type = "sv", typeStr = "瑞典"))
            types.add(XmlType(type = "ar", typeStr = "阿拉伯"))
            types.add(XmlType(type = "sk", typeStr = "斯洛伐克"))
            types.add(XmlType(type = "fi", typeStr = "芬兰"))
            types.add(XmlType(type = "sr", typeStr = "塞尔维亚"))
            types.add(XmlType(type = "hr", typeStr = "克罗地亚"))
            return types
        }

    private val carpalConfig: ArrayList<XmlType>
        get() {
            val types: ArrayList<XmlType> = arrayListOf()
            types.add(XmlType(type = "zh-rCN", typeStr = "zh")) //中文特殊类型,不需要修改typeStr
            types.add(XmlType(type = DEFAULT_LAN, typeStr = "校正英文"))
            types.add(XmlType(type = "zh-rHK", typeStr = "繁体中文"))
            types.add(XmlType(type = "ja-rJP", typeStr = "日语"))
            types.add(XmlType(type = "ru-rRU", typeStr = "俄语"))
            types.add(XmlType(type = "de-rDE", typeStr = "德语"))
            types.add(XmlType(type = "es-rES", typeStr = "西班牙语"))
            types.add(XmlType(type = "pt-rPT", typeStr = "葡萄牙语"))
            types.add(XmlType(type = "fr-rFR", typeStr = "法语"))
            types.add(XmlType(type = "it-rIT", typeStr = "意大利语"))
            types.add(XmlType(type = "pl-rPL", typeStr = "波兰语"))
            types.add(XmlType(type = "cs-rCZ", typeStr = "捷克"))
            types.add(XmlType(type = "ko-rKR", typeStr = "韩语"))
            types.add(XmlType(type = "tr-rTR", typeStr = "土耳其"))
            types.add(XmlType(type = "sk-rSK", typeStr = "斯洛伐克"))
            types.add(XmlType(type = "fi-rFI", typeStr = "芬兰"))
            types.add(XmlType(type = "sr-rRS", typeStr = "塞尔维亚"))
            types.add(XmlType(type = "hr-rHR", typeStr = "克罗地亚"))
            return types
        }
}