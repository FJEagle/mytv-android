package top.yogiczy.mytv.core.data.xxx.bean

class QmExtraResult {
    // 可选值 : "true", "false"
    // 为便于后续拓展，用逗号分隔多个值
    var hybridMode: String? = null

    // 文本内容格式 ，格式：“设备号:%s”
    var overlayTextFormatter: String? = null

    // 字体颜色，格式：“#RRGGBB”
    var overlayTextColor: String? = null

    // 字体大小，格式：“14”
    var overlayTextFont: String? = null

    // 格式："x,y,width,height"或者"false"
    var overlayTextPos: String? = null

    // 格式："x,y,width,height"或者"false"
    var overlayImagePos: String? = null
}