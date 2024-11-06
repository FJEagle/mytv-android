package top.yogiczy.mytv.core.data.xxx.common.string

/**
 * @author NieYong(ny @ xiangjiabao.com)
 * @date 2022/6/13
 * @Decription TODO
 */
class CommaSeparatorStringHelper {
    /**
     * 通用的判断 给定的字符值strValues 是否包含给定匹配的值
     * 此方法支持 strValues 值是由","分隔的多个值组成
     * 会自动trim空格
     * @param strValues
     * @return
     */
    fun isStrContainsValue(strValues: String?, matchValue: String): Boolean {
        if (strValues == null) {
            return false
        }
        //兼容多个值的处理
        val values = strValues.trim { it <= ' ' }.split(VALUE_COMMA_SEPARATOR).toTypedArray()
        return isArrayContainsValue(values, matchValue)
    }

    /**
     * 2024.4.7： 增加输入是已经split好的数组的判断支持
     */
    fun isArrayContainsValue(values: Array<String>? , matchValue: String): Boolean {
        //兼容多个值的处理
        if (values == null || values.size <= 0) {
            return false
        }
        for (v in values) {
            if (matchValue.trim { it <= ' ' } == v.trim { it <= ' ' }) {
                return true
            }
        }
        return false
    }

    /**
     * 在已有的字符串基础上，追加将多个值，合并成逗号分隔的总的字符串
     * @param valueArr
     * @return
     */
    fun <T> appendValues(originalValue: String?, vararg valueArr: T): String? {
        if (valueArr == null || valueArr.size == 0) {
            return originalValue
        }
        var initialStr = ""
        if (originalValue != null) {
            initialStr = originalValue
        }
        val builder = StringBuilder(initialStr)
        for (i in valueArr.indices) {
            val valueStr = valueArr[i].toString()
            // 避免重复的追加
            if (isStrContainsValue(originalValue, valueStr)) {
                continue
            }
            builder.append(VALUE_COMMA_SEPARATOR).append(valueStr)
        }
        return builder.toString()
    }

    /**
     * 将多个值，合并成逗号分隔的总的字符串
     * @param valueArr
     * @return
     */
    fun <T> joinValues(vararg valueArr: T?): String {
        if (valueArr == null || valueArr.size == 0) {
            return ""
        }
        var initialStr = ""
        if (valueArr[0] != null) {
            initialStr = valueArr[0].toString()
        }
        val builder = StringBuilder(initialStr)
        for (i in 1 until valueArr.size) {
            builder.append(VALUE_COMMA_SEPARATOR).append(
                valueArr[i]
            )
        }
        return builder.toString()
    }

    /**
     * 将逗号分隔的字符串，分割成值的数组
     * @param commaStr
     * @return
     */
    fun splitStr(commaStr: String?): Array<String> {
        return if (commaStr.isNullOrBlank()) {
            arrayOf()
        } else commaStr.split(VALUE_COMMA_SEPARATOR)
            .toTypedArray()
    }

    companion object {
        /**
         * 一般多个值的字符串内容，用“，”来分隔
         */
        var VALUE_COMMA_SEPARATOR = ","
    }
}