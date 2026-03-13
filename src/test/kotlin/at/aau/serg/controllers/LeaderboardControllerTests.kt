package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as whenever // when is a reserved keyword in Kotlin

class LeaderboardControllerTests {

    private lateinit var mockedService: GameResultService
    private lateinit var controller: LeaderboardController

    @BeforeEach
    fun setup() {
        mockedService = mock<GameResultService>()
        controller = LeaderboardController(mockedService)
    }

    @Test
    fun test_getLeaderboard_correctScoreSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 15, 10.0)
        val third = GameResult(3, "third", 10, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(second, first, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(first, res[0])
        assertEquals(second, res[1])
        assertEquals(third, res[2])
    }

    @Test
    fun test_getLeaderboard_sameScore_CorrectIdSorting() {
        val first = GameResult(1, "first", 20, 20.0)
        val second = GameResult(2, "second", 20, 10.0)
        val third = GameResult(3, "third", 20, 15.0)

        whenever(mockedService.getGameResults()).thenReturn(listOf(first, second, third))

        val res: List<GameResult> = controller.getLeaderboard(null)

        verify(mockedService).getGameResults()
        assertEquals(3, res.size)
        assertEquals(second, res[0])
        assertEquals(third, res[1])
        assertEquals(first, res[2])
    }

    // New tests
    @Test
    fun test_getLeaderboard_withRank_returnsSublist() {
        // Make 10 players. #1 has 100 points, #10 has 91 points
        val results = (1..10).map { i -> GameResult(i.toLong(), "Player$i", 101 - i, 10.0) }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // get rank = 5. Should return 7 players: 2,3,4 -5- 6,7,8
        val res = controller.getLeaderboard(5)

        assertEquals(7, res.size)
        assertEquals("Player2", res[0].playerName) // -3 positions up (rank 2)
        assertEquals("Player5", res[3].playerName) // rank 5
        assertEquals("Player8", res[6].playerName) // +3 positions down (rank 8)
    }

    @Test
    fun test_getLeaderboard_withRankNearTop_doesNotGoOutOfBounds() {
        val results = (1..5).map { i -> GameResult(i.toLong(), "Player$i", 101 - i, 10.0) }
        whenever(mockedService.getGameResults()).thenReturn(results)

        // get rank = 2. Up is only 1 player, down 3 players. Total 5 players.
        val res = controller.getLeaderboard(2)

        assertEquals(5, res.size)
        assertEquals("Player1", res[0].playerName) // Highest (rank 1)
        assertEquals("Player5", res[4].playerName) // Lowest (rank 5)
    }

    @Test
    fun test_getLeaderboard_invalidRank_throwsBadRequest() {
        val results = listOf(GameResult(1, "Player", 100, 10.0))
        whenever(mockedService.getGameResults()).thenReturn(results)

        // If rank = 0
        val exceptionZero = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(0)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exceptionZero.statusCode)

        // If rank(2) > NumberOfPlayers(1)
        val exceptionTooHigh = assertThrows<ResponseStatusException> {
            controller.getLeaderboard(2)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exceptionTooHigh.statusCode)
    }

}