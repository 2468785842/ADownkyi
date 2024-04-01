package com.mgws.adownkyi.core.bilibili.utils


/**
 * 解析输入的字符串
 * 支持的格式有:
 * 1. av号：av170001, AV170001, https://www.bilibili.com/video/av170001
 * 2. BV号：BV17x411w7KC, https://www.bilibili.com/video/BV17x411w7KC, https://b23.tv/BV17x411w7KC
 * 3. 番剧（电影、电视剧）ss号：ss32982, SS32982, https://www.bilibili.com/bangumi/play/ss32982
 * 4. 番剧（电影、电视剧）ep号：ep317925, EP317925, https://www.bilibili.com/bangumi/play/ep317925
 * 5. 番剧（电影、电视剧）md号：md28228367, MD28228367, https://www.bilibili.com/bangumi/media/md28228367
 * 6. 课程ss号：https://www.bilibili.com/cheese/play/ss205
 * 7. 课程ep号：https://www.bilibili.com/cheese/play/ep3489
 * 8. 收藏夹：ml1329019876, ML1329019876, https://www.bilibili.com/medialist/detail/ml1329019876 https://www.bilibili.com/medialist/play/ml1329019876/
 * 9. 用户空间：uid928123, UID928123, uid:928123, UID:928123, https://space.bilibili.com/928123
 **/
object ParseEntrance {
    private const val WWW_URL = "https://www.bilibili.com"
    private const val ShareWwwUrl = "https://www.bilibili.com/s"
    private const val ShortUrl = "https://b23.tv/"
    private const val MobileUrl = "https://m.bilibili.com"

    private const val SpaceUrl = "https://space.bilibili.com"

    private const val VideoUrl = "$WWW_URL/video/"
    private const val BangumiUrl = "$WWW_URL/bangumi/play/"
    private const val BangumiMediaUrl = "$WWW_URL/bangumi/media/"
    private const val CheeseUrl = "$WWW_URL/cheese/play/"
    private const val FavoritesUrl1 = "$WWW_URL/medialist/detail/"
    private const val FavoritesUrl2 = "$WWW_URL/medialist/play/"


    // -------------------------------------------------------------------
    // ---------------------------- 视频 ----------------------------------
    // -------------------------------------------------------------------

    /**
     * 是否为Av Id
     */
    fun isAvId(input: String): Boolean = isIntId(input, "av")

    /**
     * 是否为Av Url
     */
    fun isAvUrl(input: String): Boolean = isAvId(getVideoId(input))

    /**
     * 获取Av Id
     */
    fun getAvId(input: String): Long = when {
        isAvId(input) -> input.substring(2).toLong()
        isAvUrl(input) -> getVideoId(input).substring(2).toLong()
        else -> -1
    }

    /**
     * 是否为Bv Id
     */
    fun isBvId(input: String): Boolean = input.startsWith("BV") && input.length == 12

    /**
     * 是否为Bv Url
     */
    fun isBvUrl(input: String): Boolean = isBvId(getVideoId(input))

    /**
     * 获取Bv Id
     */
    fun getBvId(input: String): String = when {
        isBvId(input) -> input
        isBvUrl(input) -> getVideoId(input)
        else -> ""
    }

    // -------------------------------------------------------------------
    // ---------------------------- 番剧(电影,电视剧) -----------------------
    // -------------------------------------------------------------------

    /**
     * 是否为番剧 Season Id
     */
    fun isBangumiSeasonId(input: String): Boolean = isIntId(input, "ss")

    /**
     * 是否为番剧 Season Url
     */
    fun isBangumiSeasonUrl(input: String): Boolean = isBangumiSeasonId(getBangumiId(input))

    /**
     * 获取番剧 Season Id
     */
    fun getBangumiSeasonId(input: String): Long = when {
        isBangumiSeasonId(input) -> input.substring(2).toLong()
        isBangumiSeasonUrl(input) -> getBangumiId(input).substring(2).toLong()
        else -> -1
    }

    /**
     * 是否为番剧Episode Id
     */
    fun isBangumiEpisodeId(input: String): Boolean = isIntId(input, "ep")

    /**
     * 是否为番剧Episode Url
     */
    fun isBangumiEpisodeUrl(input: String): Boolean = isBangumiEpisodeId(getBangumiId(input))

    /**
     * 获取番剧 Episode Id
     */
    fun getBangumiEpisodeId(input: String): Long = when {
        isBangumiEpisodeId(input) -> input.substring(2).toLong()
        isBangumiEpisodeUrl(input) -> getBangumiId(input).substring(2).toLong()
        else -> -1
    }

    /**
     * 是否为番剧Media Id
     */
    fun isBangumiMediaId(input: String): Boolean = isIntId(input, "md")

    /**
     * 是否为番剧Media Url
     */
    fun isBangumiMediaUrl(input: String): Boolean = isBangumiMediaId(getBangumiId(input))

    /**
     * 获取番剧Media Id
     */
    fun getBangumiMediaId(input: String): Long = when {
        isBangumiMediaId(input) -> input.substring(2).toLong()
        isBangumiMediaUrl(input) -> getBangumiId(input).substring(2).toLong()
        else -> -1
    }

    /**
     * 是否为课程Season Url
     */
    fun isCheeseSeasonUrl(input: String): Boolean = isIntId(getCheeseId(input), "ss")

    /**
     * 获取课程Season Id
     */
    fun getCheeseSeasonId(input: String): Long = when {
        isCheeseSeasonUrl(input) -> getCheeseId(input).substring(2).toLong()
        else -> -1
    }

    /**
     * 是否为课程Episode url
     */
    fun isCheeseEpisodeUrl(input: String): Boolean = isIntId(getCheeseId(input), "ep")

    /**
     * 获取课程Episode Id
     */
    fun getCheeseEpisodeId(input: String): Long = when {
        isCheeseEpisodeUrl(input) -> getCheeseId(input).substring(2).toLong()
        else -> -1
    }

    //-----------------------------------------------------------
    //------------------------------收藏夹------------------------
    //-----------------------------------------------------------

    /**
     * 是否为收藏夹 Id
     */
    fun isFavoritesId(input: String): Boolean = isIntId(input, "ml")

    /**
     * 是否为收藏夹 Url
     */
    fun isFavoritesUrl(input: String): Boolean =
        isFavoritesUrl1(input) && isFavoritesUrl2(input)

    /**
     * 是否为收藏夹 Url1
     */
    fun isFavoritesUrl1(input: String): Boolean =
        isFavoritesId(getId(input, FavoritesUrl1) ?: "")

    /**
     * 是否为收藏夹 Url2
     */
    fun isFavoritesUrl2(input: String): Boolean =
        isFavoritesId((getId(input, FavoritesUrl2) ?: "").split('/')[0])

    /**
     * 获取收藏夹 Id
     */
    fun getFavoritesId(input: String): Long = when {
        isFavoritesId(input) ->
            input.substring(2).toLong()

        isFavoritesUrl1(input) ->
            getId(input, FavoritesUrl1)!!.substring(2).toLong()

        isFavoritesUrl2(input) ->
            getId(input, FavoritesUrl2)!!.substring(2).split('/')[0].toLong()

        else -> -1
    }

    //-----------------------------------------------------------
    //----------------------------用户空间------------------------
    //-----------------------------------------------------------


    /**
     * 是否为用户 Id
     */
    fun isUserId(input: String): Boolean = when {
        input.lowercase().startsWith("uid:") ->
            Regex("^\\d+$").matches(input.substring(4))

        input.lowercase().startsWith("uid") ->
            Regex("^\\d+$").matches(input.substring(3))

        else -> false
    }

    /**
     * 是否为用户 Url
     */
    fun isUserUrl(input: String): Boolean = input.contains(SpaceUrl)

    /**
     * 获取用户 Mid
     */
    fun getUserId(input: String): Long = when {
        input.lowercase().startsWith("uid:") ->
            input.substring(4).toLong()

        input.lowercase().startsWith("uid") ->
            input.substring(3).toLong()

        isUserUrl(input) -> {
            val regex = Regex("\\d+")
            val url = deleteUrlParam(enableHttps(input)!!)
            regex.find(url)?.value?.toLong() ?: -1
        }

        else -> -1
    }


    /**
     * 字符串是否为url
     */
    private fun isUrl(input: String): Boolean =
        input.startsWith("http://") || input.startsWith("https://")

    /**
     * 将http转为https
     */
    private fun enableHttps(url: String): String? = when {
        isUrl(url) -> url.replace("http://", "https://")
        else -> null
    }

    /**
     * 去除url中的参数
     */
    private fun deleteUrlParam(url: String): String {
        val strList = url.split('?')
        return when {
            strList[0].endsWith("/") -> strList[0].trimEnd('/')
            else -> strList[0]
        }
    }

    /**
     * 从url获取视频 id
     */
    private fun getVideoId(input: String): String = getId(input, VideoUrl) ?: ""

    /**
     * 从url获取番剧 id
     */
    private fun getBangumiId(input: String): String =
        getId(input, BangumiUrl) ?: getId(input, BangumiMediaUrl) ?: ""

    /**
     * 从url获取课程 id
     */
    private fun getCheeseId(input: String): String = getId(input, CheeseUrl) ?: ""

    /**
     * 是否为数字型 id
     */
    private fun isIntId(input: String, prefix: String): Boolean = when {
        input.lowercase().startsWith(prefix) ->
            Regex("^\\d+\$").matches(input.substring(2))

        else -> false
    }


    /**
     * 从url中获取 id
     */
    private fun getId(input: String, baseUrl: String): String? {
        if (!isUrl(input)) {
            return null
        }

        var url: String = enableHttps(input)!!
        url = deleteUrlParam(url)
        url = url.replace(ShareWwwUrl, WWW_URL)
        url = url.replace(MobileUrl, WWW_URL)

        url = when {
            url.contains("b23.tv/ss") || url.contains("b23.tv/ep") ->
                url.replace(ShortUrl, BangumiUrl)

            else -> url.replace(ShortUrl, VideoUrl)
        }

        return when {
            url.startsWith(baseUrl) -> url.replace(baseUrl, "")
            else -> null
        }

    }
}