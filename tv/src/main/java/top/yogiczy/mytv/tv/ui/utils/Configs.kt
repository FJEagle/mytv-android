package top.yogiczy.mytv.tv.ui.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.core.data.utils.SP
import top.yogiczy.mytv.tv.MyTVApplication
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.screens.videoplayer.VideoPlayerDisplayMode
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.Locale
import java.util.Scanner

/**
 * 应用配置
 */
object Configs {
    private val log = Logger.create(javaClass.simpleName)

    enum class KEY {
        /** ==================== 应用 ==================== */
        /** 开机自启 */
        APP_BOOT_LAUNCH,

        /** 上一次最新版本 */
        APP_LAST_LATEST_VERSION,

        /** 协议已同意 */
        APP_AGREEMENT_AGREED,

        /** ==================== 调式 ==================== */
        /** 显示fps */
        DEBUG_SHOW_FPS,

        /** 播放器详细信息 */
        DEBUG_SHOW_VIDEO_PLAYER_METADATA,

        /** 显示布局网格 */
        DEBUG_SHOW_LAYOUT_GRIDS,

        /** ==================== 直播源 ==================== */
        /** 上一次频道序号 */
        IPTV_LAST_CHANNEL_IDX,

        /** 换台反转 */
        IPTV_CHANNEL_CHANGE_FLIP,

        /** 当前直播源 */
        IPTV_SOURCE_CURRENT,

        /** 直播源列表 */
        IPTV_SOURCE_LIST,

        /** 直播源缓存时间（毫秒） */
        IPTV_SOURCE_CACHE_TIME,

        /** 直播源可播放host列表 */
        IPTV_PLAYABLE_HOST_LIST,

        /** 是否启用数字选台 */
        IPTV_CHANNEL_NO_SELECT_ENABLE,

        /** 是否启用直播源频道收藏 */
        IPTV_CHANNEL_FAVORITE_ENABLE,

        /** 显示直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST_VISIBLE,

        /** 直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST,

        /** 直播源频道收藏换台边界跳出 */
        IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT,

        /** 直播源分组隐藏列表 */
        IPTV_CHANNEL_GROUP_HIDDEN_LIST,

        /** 混合模式 */
        IPTV_HYBRID_MODE,

        /** ==================== 节目单 ==================== */
        /** 启用节目单 */
        EPG_ENABLE,

        /** 当前节目单来源 */
        EPG_SOURCE_CURRENT,

        /** 节目单来源列表 */
        EPG_SOURCE_LIST,

        /** 节目单刷新时间阈值（小时） */
        EPG_REFRESH_TIME_THRESHOLD,

        /** 节目预约列表 */
        EPG_CHANNEL_RESERVE_LIST,

        /** ==================== 界面 ==================== */
        /** 显示节目进度 */
        UI_SHOW_EPG_PROGRAMME_PROGRESS,

        /** 显示常驻节目进度 */
        UI_SHOW_EPG_PROGRAMME_PERMANENT_PROGRESS,

        /** 显示台标 */
        UI_SHOW_CHANNEL_LOGO,

        /** 使用经典选台界面 */
        UI_USE_CLASSIC_PANEL_SCREEN,

        /** 界面密度缩放比例 */
        UI_DENSITY_SCALE_RATIO,

        /** 界面字体缩放比例 */
        UI_FONT_SCALE_RATIO,

        /** 时间显示模式 */
        UI_TIME_SHOW_MODE,

        /** 焦点优化 */
        UI_FOCUS_OPTIMIZE,

        /** 自动关闭界面延时 */
        UI_SCREEN_AUTO_CLOSE_DELAY,

        /** ==================== 更新 ==================== */
        /** 更新强提醒 */
        UPDATE_FORCE_REMIND,

        /** 更新通道 */
        UPDATE_CHANNEL,

        /** ==================== 播放器 ==================== */
        /** 播放器 自定义ua */
        VIDEO_PLAYER_USER_AGENT,

        /** 播放器 加载超时 */
        VIDEO_PLAYER_LOAD_TIMEOUT,

        /** 播放器 显示模式 */
        VIDEO_PLAYER_DISPLAY_MODE,
    }

    /** ==================== 应用 ==================== */
    /** 开机自启 */
    var appBootLaunch: Boolean
        get() = SP.getBoolean(KEY.APP_BOOT_LAUNCH.name, false)
        set(value) = SP.putBoolean(KEY.APP_BOOT_LAUNCH.name, value)

    /** 上一次最新版本 */
    var appLastLatestVersion: String
        get() = SP.getString(KEY.APP_LAST_LATEST_VERSION.name, "")
        set(value) = SP.putString(KEY.APP_LAST_LATEST_VERSION.name, value)

    /** 协议已同意 */
    var appAgreementAgreed: Boolean
        get() = SP.getBoolean(KEY.APP_AGREEMENT_AGREED.name, false)
        set(value) = SP.putBoolean(KEY.APP_AGREEMENT_AGREED.name, value)

    /** ==================== 调式 ==================== */
    /** 显示fps */
    var debugShowFps: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_FPS.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_FPS.name, value)

    /** 播放器详细信息 */
    var debugShowVideoPlayerMetadata: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_VIDEO_PLAYER_METADATA.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_VIDEO_PLAYER_METADATA.name, value)

    /** 显示布局网格 */
    var debugShowLayoutGrids: Boolean
        get() = SP.getBoolean(KEY.DEBUG_SHOW_LAYOUT_GRIDS.name, false)
        set(value) = SP.putBoolean(KEY.DEBUG_SHOW_LAYOUT_GRIDS.name, value)

    /** ==================== 直播源 ==================== */
    /** 上一次直播源序号 */
    var iptvLastChannelIdx: Int
        get() = SP.getInt(KEY.IPTV_LAST_CHANNEL_IDX.name, 0)
        set(value) = SP.putInt(KEY.IPTV_LAST_CHANNEL_IDX.name, value)

    /** 换台反转 */
    var iptvChannelChangeFlip: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, false)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_CHANGE_FLIP.name, value)

    /** 当前直播源 */
    // 2024.11.5: 强制使用自定义配置
    var iptvSourceCurrent: IptvSource
        get() = _customIptvSource ?: Json.decodeFromString(SP.getString(KEY.IPTV_SOURCE_CURRENT.name, "")
            .ifBlank { Json.encodeToString(Constants.IPTV_SOURCE_LIST.first()) })
        set(value) = SP.putString(KEY.IPTV_SOURCE_CURRENT.name, Json.encodeToString(value))

    private val _customIptvSource: IptvSource? by lazy {
        loadCustomIptvSource()
    }
    private fun loadCustomIptvSource(): IptvSource?{
        // 2024.11.5: 暂时兼容tvbox的配置方式，也没有新的配置？
        return loadCustomIptvSourceByTvboxConfig()
    }

    /**
     * 2024.11.5: 移植原来的tvbox的自定义配置方式，兼容已有设备的tvbox方式的配置
     */
    private fun loadCustomIptvSourceByTvboxConfig(): IptvSource?{
        // 2023.11.23：尝试从动态下载、ROM和应用内等查找配置文件
        val context = MyTVApplication.application
        val jsonStr: String = readCustomJsonFileWrapper(context)
        try {
            val liveUrlFinal = parseJson(jsonStr)
            return IptvSource(
                name = "xxx自定义",
                url = liveUrlFinal,
                isLocal = false
            )
        } catch (th: Throwable) {
            th.printStackTrace()
            return null
        }
    }

    /**
     * 2024.11.5： 从原tvbox代码中移植过来，删减
     */
    private fun parseJson(jsonStr: String): String {
        val infoJson =Json.parseToJsonElement(jsonStr).jsonObject

        var liveURL_final: String = ""
        val context = MyTVApplication.application
        try {
            if ("lives" in infoJson && infoJson["lives"]?.jsonArray != null) {
                val livesOBJ = infoJson["lives"]?.jsonArray?.get(0)?.jsonObject
                val lives: String = livesOBJ.toString()
                log.d("lives:$lives")
                val index = lives.indexOf("proxy://")
                if (index != -1) {
                    val endIndex = lives.lastIndexOf("\"")
                    var url = lives.substring(index, endIndex)
                    url = checkReplaceProxy(url)
                    log.d("url:$url")

                    //clan
                    val extUrl = Uri.parse(url).getQueryParameter("ext")
                    log.d("ext:$extUrl")
                    if (extUrl != null && !extUrl.isEmpty()) {
                        var extUrlFix: String
                        extUrlFix = if (extUrl.startsWith("http") || extUrl.startsWith("clan://")) {
                            extUrl
                        } else {
                            String(Base64.decode(extUrl, Base64.DEFAULT or Base64.URL_SAFE or Base64.NO_WRAP), charset("UTF-8"))
                        }

                        // takagen99: Capture Live URL into Config
                        Log.d("tv", "Live URL :$extUrlFix")

                        // 2023.11.23: 附加额外的上下文参数
                        //api携带的app基本信息参数的参数名称KEY
                        val API_APP_BASIC_INFO_PARAM_KEY_DEVICE_MODEL = "deviceModel"
                        val API_APP_BASIC_INFO_PARAM_KEY_ROM_VERSION = "romVersion"

                        /**
                         * 安卓系统提供的build厂商信息
                         */
                        val API_APP_BASIC_INFO_PARAM_KEY_BUILD_MANUFACTURER = "buildManufacturer"

                        /**
                         * 安卓系统提供的build品牌信息
                         */
                        val API_APP_BASIC_INFO_PARAM_KEY_BUILD_BRAND = "buildBrand"
                        val API_APP_BASIC_INFO_PARAM_KEY_PKG_NAME = "packageName"
                        val API_APP_BASIC_INFO_PARAM_KEY_VERSION = "version"
                        val API_APP_BASIC_INFO_PARAM_KEY_VERSION_CODE = "versionCode"
                        val API_APP_BASIC_INFO_PARAM_KEY_DEVICE_UA = "deviceUA"
                        val packageName: String = context.getPackageName()
                        val packageInfo: PackageInfo = context.getPackageManager().getPackageInfo(packageName, 0)
                        val uaStr = String.format(
                            Locale.getDefault(), "%s/%s/%s/%s/%s/%d",
                            Build.MANUFACTURER,
                            Build.BRAND,
                            Build.MODEL,
                            Build.VERSION.RELEASE,
                            Build.DISPLAY,
                            Build.VERSION.SDK_INT
                        )
                        // 使用 Uri 类来处理 URL
                        val builder = Uri.parse(extUrlFix).buildUpon()
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_DEVICE_MODEL,
                            Build.MODEL
                        )
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_ROM_VERSION,
                            Build.DISPLAY
                        )
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_BUILD_MANUFACTURER,
                            Build.MANUFACTURER
                        )
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_BUILD_BRAND,
                            Build.BRAND
                        )
                        builder.appendQueryParameter(API_APP_BASIC_INFO_PARAM_KEY_DEVICE_UA, uaStr)
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_PKG_NAME,
                            packageName
                        )
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_VERSION,
                            packageInfo.versionName
                        )
                        builder.appendQueryParameter(
                            API_APP_BASIC_INFO_PARAM_KEY_VERSION_CODE,
                            packageInfo.versionCode.toString() + ""
                        )
                        val urlWithContext = builder.build().toString()
                        Log.d("tv", "Live URL withContext:$urlWithContext")

                        // Final Live URL
                        // liveURL_final = extUrlFix;
                        liveURL_final = urlWithContext
                    }
                }
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
        return liveURL_final
    }

    private fun checkReplaceProxy(urlOri: String): String {
        if (urlOri.startsWith("proxy://")) return urlOri.replace("proxy://", "http://127.0.0.1:8080/proxy?")
        return urlOri
    }

    fun readJsonFile(context: Context, resourceId: Int): String {
        val jsonStringBuilder = StringBuilder()

        try {
            // 获取 Resources 对象
            val resources = context.resources

            // 打开指定资源的 InputStream
            val inputStream = resources.openRawResource(resourceId)

            // 使用 Scanner 读取 InputStream 中的内容
            val scanner = Scanner(inputStream)
            while (scanner.hasNextLine()) {
                jsonStringBuilder.append(scanner.nextLine())
            }

            // 关闭 InputStream 和 Scanner
            inputStream.close()
            scanner.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 返回 JSON 字符串
        return jsonStringBuilder.toString()
    }

    @Throws(Exception::class)
    private fun readFileContent(file: File): String {
        val content = StringBuilder()

        BufferedReader(FileReader(file)).use { reader ->
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                content.append(line).append("\n")
            }
        }
        return content.toString()
    }

    /**
     * 下载到sdcard/tvbox/tv.txt > 下载到sdcard/tv.txt > ROM/custom/tvbox/tv.txt > raw/tv.txt
     */
    private fun readCustomJsonFileWrapper(context: Context): String {
        // 避免可能动态下载存储和ROM存储的位置
        val pathsToCheck = arrayOf( // 优选这个
            Environment.getExternalStorageDirectory().toString() + "/xxx/xx_v_t.txt",
            Environment.getExternalStorageDirectory().toString() + "/xxx/tv.txt",
            Environment.getExternalStorageDirectory().toString() + "/tvbox/tv.txt",
            Environment.getExternalStorageDirectory().toString() + "/xx_v_t.txt",
            Environment.getExternalStorageDirectory().toString() + "/tv.txt",  // 优选这个
            "/system/custom/xxx/xx_v_t.txt",
            "/system/custom/xxx/tv.txt",
            "/system/custom/tvbox/tv.txt",
            "/system/custom/xx_v_t.txt",
            "/system/custom/tv.txt"
        )

        for (path in pathsToCheck) {
            try {
                val file = File(path)
                if (file.exists()) {
                    Log.d("tv", "found:" + file.absolutePath)
                    // 如果文件存在，尝试读取文件内容
                    return readFileContent(file)
                }
            } catch (e: SecurityException) {
                // 捕获没有权限的异常，打印日志，不中断流程
                Log.e("FileChecker", "Permission denied: $path")
            } catch (e: Exception) {
                // 捕获其他异常，打印日志，不中断流程
                Log.e("FileChecker", "Error reading file: $path", e)
            }
        }

        // 最后：使用内置的资源文件
        val jsonStr = readJsonFile(context, R.raw.tv_legacy_tvbox)
        return jsonStr
    }

    /** 直播源列表 */
    var iptvSourceList: IptvSourceList
        get() = Json.decodeFromString(
            SP.getString(KEY.IPTV_SOURCE_LIST.name, Json.encodeToString(IptvSourceList()))
        )
        set(value) = SP.putString(KEY.IPTV_SOURCE_LIST.name, Json.encodeToString(value))

    /** 直播源缓存时间（毫秒） */
    var iptvSourceCacheTime: Long
        get() = SP.getLong(KEY.IPTV_SOURCE_CACHE_TIME.name, Constants.IPTV_SOURCE_CACHE_TIME)
        set(value) = SP.putLong(KEY.IPTV_SOURCE_CACHE_TIME.name, value)

    /** 直播源可播放host列表 */
    var iptvPlayableHostList: Set<String>
        get() = SP.getStringSet(KEY.IPTV_PLAYABLE_HOST_LIST.name, emptySet())
        set(value) = SP.putStringSet(KEY.IPTV_PLAYABLE_HOST_LIST.name, value)

    /** 是否启用数字选台 */
    var iptvChannelNoSelectEnable: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_NO_SELECT_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_NO_SELECT_ENABLE.name, value)

    /** 是否启用直播源频道收藏 */
    var iptvChannelFavoriteEnable: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_ENABLE.name, value)

    /** 显示直播源频道收藏列表 */
    var iptvChannelFavoriteListVisible: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_LIST_VISIBLE.name, false)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_LIST_VISIBLE.name, value)

    /** 直播源频道收藏列表 */
    var iptvChannelFavoriteList: Set<String>
        get() = SP.getStringSet(KEY.IPTV_CHANNEL_FAVORITE_LIST.name, emptySet())
        set(value) = SP.putStringSet(KEY.IPTV_CHANNEL_FAVORITE_LIST.name, value)

    /** 直播源频道收藏换台边界跳出 */
    var iptvChannelFavoriteChangeBoundaryJumpOut: Boolean
        get() = SP.getBoolean(KEY.IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT.name, true)
        set(value) = SP.putBoolean(KEY.IPTV_CHANNEL_FAVORITE_CHANGE_BOUNDARY_JUMP_OUT.name, value)

    /** 直播源分组隐藏列表 */
    var iptvChannelGroupHiddenList: Set<String>
        get() = SP.getStringSet(KEY.IPTV_CHANNEL_GROUP_HIDDEN_LIST.name, emptySet())
        set(value) = SP.putStringSet(KEY.IPTV_CHANNEL_GROUP_HIDDEN_LIST.name, value)

    /** 混合模式 */
    var iptvHybridMode: IptvHybridMode
        get() = IptvHybridMode.fromValue(
            SP.getInt(KEY.IPTV_HYBRID_MODE.name, IptvHybridMode.DISABLE.value)
        )
        set(value) = SP.putInt(KEY.IPTV_HYBRID_MODE.name, value.value)

    /** ==================== 节目单 ==================== */
    /** 启用节目单 */
    var epgEnable: Boolean
        get() = SP.getBoolean(KEY.EPG_ENABLE.name, true)
        set(value) = SP.putBoolean(KEY.EPG_ENABLE.name, value)

    /** 当前节目单来源 */
    var epgSourceCurrent: EpgSource
        get() = Json.decodeFromString(SP.getString(KEY.EPG_SOURCE_CURRENT.name, "")
            .ifBlank { Json.encodeToString(Constants.EPG_SOURCE_LIST.first()) })
        set(value) = SP.putString(KEY.EPG_SOURCE_CURRENT.name, Json.encodeToString(value))

    /** 节目单来源列表 */
    var epgSourceList: EpgSourceList
        get() = Json.decodeFromString(
            SP.getString(KEY.EPG_SOURCE_LIST.name, Json.encodeToString(EpgSourceList()))
        )
        set(value) = SP.putString(KEY.EPG_SOURCE_LIST.name, Json.encodeToString(value))

    /** 节目单刷新时间阈值（小时） */
    var epgRefreshTimeThreshold: Int
        get() = SP.getInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, Constants.EPG_REFRESH_TIME_THRESHOLD)
        set(value) = SP.putInt(KEY.EPG_REFRESH_TIME_THRESHOLD.name, value)

    /** 节目预约列表 */
    var epgChannelReserveList: EpgProgrammeReserveList
        get() = Json.decodeFromString(
            SP.getString(
                KEY.EPG_CHANNEL_RESERVE_LIST.name, Json.encodeToString(EpgProgrammeReserveList())
            )
        )
        set(value) = SP.putString(KEY.EPG_CHANNEL_RESERVE_LIST.name, Json.encodeToString(value))

    /** ==================== 界面 ==================== */
    /** 显示节目进度 */
    var uiShowEpgProgrammeProgress: Boolean
        get() = SP.getBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PROGRESS.name, true)
        set(value) = SP.putBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PROGRESS.name, value)

    /** 显示常驻节目进度 */
    var uiShowEpgProgrammePermanentProgress: Boolean
        get() = SP.getBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PERMANENT_PROGRESS.name, false)
        set(value) = SP.putBoolean(KEY.UI_SHOW_EPG_PROGRAMME_PERMANENT_PROGRESS.name, value)

    /** 显示台标 */
    var uiShowChannelLogo: Boolean
        get() = SP.getBoolean(KEY.UI_SHOW_CHANNEL_LOGO.name, false)
        set(value) = SP.putBoolean(KEY.UI_SHOW_CHANNEL_LOGO.name, value)

    /** 使用经典选台界面 */
    var uiUseClassicPanelScreen: Boolean
        get() = SP.getBoolean(KEY.UI_USE_CLASSIC_PANEL_SCREEN.name, false)
        set(value) = SP.putBoolean(KEY.UI_USE_CLASSIC_PANEL_SCREEN.name, value)

    /** 界面密度缩放比例 */
    var uiDensityScaleRatio: Float
        get() = SP.getFloat(KEY.UI_DENSITY_SCALE_RATIO.name, 0f)
        set(value) = SP.putFloat(KEY.UI_DENSITY_SCALE_RATIO.name, value)

    /** 界面字体缩放比例 */
    var uiFontScaleRatio: Float
        get() = SP.getFloat(KEY.UI_FONT_SCALE_RATIO.name, 1f)
        set(value) = SP.putFloat(KEY.UI_FONT_SCALE_RATIO.name, value)

    /** 时间显示模式 */
    var uiTimeShowMode: UiTimeShowMode
        get() = UiTimeShowMode.fromValue(
            SP.getInt(KEY.UI_TIME_SHOW_MODE.name, UiTimeShowMode.HIDDEN.value)
        )
        set(value) = SP.putInt(KEY.UI_TIME_SHOW_MODE.name, value.value)

    /** 焦点优化 */
    var uiFocusOptimize: Boolean
        get() = SP.getBoolean(KEY.UI_FOCUS_OPTIMIZE.name, true)
        set(value) = SP.putBoolean(KEY.UI_FOCUS_OPTIMIZE.name, value)

    /** 自动关闭界面延时 */
    var uiScreenAutoCloseDelay: Long
        get() =
            SP.getLong(KEY.UI_SCREEN_AUTO_CLOSE_DELAY.name, Constants.UI_SCREEN_AUTO_CLOSE_DELAY)
        set(value) = SP.putLong(KEY.UI_SCREEN_AUTO_CLOSE_DELAY.name, value)

    /** ==================== 更新 ==================== */
    /** 更新强提醒 */
    var updateForceRemind: Boolean
        get() = SP.getBoolean(KEY.UPDATE_FORCE_REMIND.name, false)
        set(value) = SP.putBoolean(KEY.UPDATE_FORCE_REMIND.name, value)

    /** 更新通道 */
    var updateChannel: String
        get() = SP.getString(KEY.UPDATE_CHANNEL.name, "stable")
        set(value) = SP.putString(KEY.UPDATE_CHANNEL.name, value)

    /** ==================== 播放器 ==================== */
    /** 播放器 自定义ua */
    var videoPlayerUserAgent: String
        get() = SP.getString(KEY.VIDEO_PLAYER_USER_AGENT.name, "").ifBlank {
            Constants.VIDEO_PLAYER_USER_AGENT
        }
        set(value) = SP.putString(KEY.VIDEO_PLAYER_USER_AGENT.name, value)

    /** 播放器 加载超时 */
    var videoPlayerLoadTimeout: Long
        get() = SP.getLong(KEY.VIDEO_PLAYER_LOAD_TIMEOUT.name, Constants.VIDEO_PLAYER_LOAD_TIMEOUT)
        set(value) = SP.putLong(KEY.VIDEO_PLAYER_LOAD_TIMEOUT.name, value)

    /** 播放器 显示模式 */
    var videoPlayerDisplayMode: VideoPlayerDisplayMode
        get() = VideoPlayerDisplayMode.fromValue(
            SP.getInt(KEY.VIDEO_PLAYER_DISPLAY_MODE.name, VideoPlayerDisplayMode.ORIGINAL.value)
        )
        set(value) = SP.putInt(KEY.VIDEO_PLAYER_DISPLAY_MODE.name, value.value)

    enum class UiTimeShowMode(val value: Int) {
        /** 隐藏 */
        HIDDEN(0),

        /** 常显 */
        ALWAYS(1),

        /** 整点 */
        EVERY_HOUR(2),

        /** 半点 */
        HALF_HOUR(3);

        companion object {
            fun fromValue(value: Int): UiTimeShowMode {
                return entries.firstOrNull { it.value == value } ?: ALWAYS
            }
        }
    }

    enum class IptvHybridMode(val value: Int) {
        /** 禁用 */
        DISABLE(0),

        /** 直播源优先 */
        IPTV_FIRST(1),

        /** 混合优先 */
        HYBRID_FIRST(2);

        companion object {
            fun fromValue(value: Int): IptvHybridMode {
                return entries.firstOrNull { it.value == value } ?: DISABLE
            }
        }
    }
}