package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import kotlin.math.max
import kotlin.math.min

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(
        @RequestParam(required = false) rank: Int?
    ):List<GameResult> {
        // Getting and Sorting List by score and time
        val sortedLeaderboard = gameResultService.getGameResults()
            .sortedWith(compareBy({ -it.score }, { it.timeInSeconds }))

        // If no rank -> return list
        if (rank == null) {
            return sortedLeaderboard
        }

        // If rank don't fit
        if (rank < 1 || rank > sortedLeaderboard.size) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Rank must be between 1 and ${sortedLeaderboard.size}"
            )
        }

        // Rank 1 is index [0]
        val targetIndex = rank - 1

        // 3 before and 3 after
        val startIndex = max(0, targetIndex - 3)
        val endIndex = min(sortedLeaderboard.size - 1, targetIndex + 3)

        // return needed part of the list
        return sortedLeaderboard.subList(startIndex, endIndex + 1)
    }
}