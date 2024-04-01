package com.mgws.adownkyi.core.bilibili.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ParseEntranceTest {

    @Test
    fun checkAv() {
        val avStrList = arrayOf("av170001", "AV170001", "https://www.bilibili.com/video/av170001")
        val avId = 170001L

        avStrList.forEach {
            val gAvId = ParseEntrance.getAvId(it)
            assertEquals(avId, gAvId)
        }
    }

    @Test
    fun checkBv() {
        val bvStrList = arrayOf(
            "BV17x411w7KC",
            "https://www.bilibili.com/video/BV17x411w7KC",
            "https://b23.tv/BV17x411w7KC"
        )
        val bvId = "BV17x411w7KC"

        bvStrList.forEach {
            val gBvId = ParseEntrance.getBvId(it)
            assertEquals(bvId, gBvId)
        }
    }

    @Test
    fun checkBangumiSeason() {
        val seasonStrList = listOf(
            "ss32982", "SS32982",
            "https://www.bilibili.com/bangumi/play/ss32982"
        )
        val seasonId = 32982L

        seasonStrList.forEach {
            val gSeasonId = ParseEntrance.getBangumiSeasonId(it)
            assertEquals(seasonId, gSeasonId)
        }
    }

    @Test
    fun checkBangumiEpisode() {
        val episodeStrList = listOf(
            "ep317925", "EP317925",
            "https://www.bilibili.com/bangumi/play/ep317925"
        )
        val episodeId = 317925L

        episodeStrList.forEach {
            val gEpisodeId = ParseEntrance.getBangumiEpisodeId(it)
            assertEquals(episodeId, gEpisodeId)
        }
    }

    @Test
    fun checkBangumiMedia() {
        val mediaStrList = listOf(
            "md28228367", "MD28228367",
            "https://www.bilibili.com/bangumi/media/md28228367"
        )
        val mediaId = 28228367L

        mediaStrList.forEach {
            val gMediaId = ParseEntrance.getBangumiMediaId(it)
            assertEquals(mediaId, gMediaId)
        }
    }

    @Test
    fun checkCheeseSeason() {
        val seasonStr = "https://www.bilibili.com/cheese/play/ss205"
        val gSeasonId = ParseEntrance.getCheeseSeasonId(seasonStr)
        assertEquals(205L, gSeasonId)
    }

    @Test
    fun checkCheeseEpisode() {
        val episodeStr = "https://www.bilibili.com/cheese/play/ep3489"
        val gEpisodeId = ParseEntrance.getCheeseEpisodeId(episodeStr)
        assertEquals(3489L, gEpisodeId)
    }


    @Test
    fun checkFavorites() {
        val favoritesStr = listOf(
            "ml1329019876", "ML1329019876",
            "https://www.bilibili.com/medialist/detail/ml1329019876",
            "https://www.bilibili.com/medialist/play/ml1329019876/"
        )
        val favoritesId = 1329019876L

        favoritesStr.forEach {
            val gFavoritesId = ParseEntrance.getFavoritesId(it)
            assertEquals(favoritesId, gFavoritesId)
        }
    }

    @Test
    fun checkUser() {
        val userStrList = listOf(
            "uid928123", "UID928123", "uid:928123", "UID:928123",
            "https://space.bilibili.com/928123"
        )
        val userId = 928123L

        userStrList.forEach {
            val gUserId = ParseEntrance.getUserId(it)
            assertEquals(userId, gUserId)
        }
    }

}