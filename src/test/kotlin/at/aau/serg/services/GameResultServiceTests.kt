package at.aau.serg.services

import at.aau.serg.models.GameResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameResultServiceTests {

    private lateinit var service: GameResultService

    @BeforeEach
    fun setup() {
        service = GameResultService()
    }

    @Test
    fun test_getGameResults_emptyList() {
        val result = service.getGameResults()

        assertEquals(emptyList<GameResult>(), result)
    }

    @Test
    fun test_addGameResult_getGameResults_containsSingleElement() {
        val gameResult = GameResult(1, "player1", 17, 15.3)

        service.addGameResult(gameResult)
        val res = service.getGameResults()

        assertEquals(1, res.size)
        assertEquals(gameResult, res[0])
    }

    @Test
    fun test_getGameResultById_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(1)

        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResultById_nonexistentId_returnsNull() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(22)

        assertNull(res)
    }

    @Test
    fun test_addGameResult_multipleEntries_correctIdAndSorting() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)

        val res = service.getGameResults()

        assertEquals(2, res.size)

        assertEquals(gameResult2, res[0])
        assertEquals(2, res[0].id)

        assertEquals(gameResult1, res[1])
        assertEquals(1, res[1].id)
    }

    // New test for tie
    @Test
    fun test_getGameResults_tiebreaker_sortByTime() {
        val gameResult1 = GameResult(0, "player1", 20, 15.3)
        val gameResult2 = GameResult(0, "player2", 20, 10.0)
        val gameResult3 = GameResult(0, "player3", 17, 5.3)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)
        service.addGameResult(gameResult3)

        val res = service.getGameResults()

        assertEquals(3, res.size)

        assertEquals(gameResult2, res[0])
        assertEquals(2, res[0].id)

        assertEquals(gameResult1, res[1])
        assertEquals(1, res[1].id)

        assertEquals(gameResult3, res[2])
        assertEquals(3, res[2].id)
    }

}