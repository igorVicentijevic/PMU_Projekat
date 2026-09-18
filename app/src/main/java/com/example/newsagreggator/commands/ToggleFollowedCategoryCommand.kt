package com.example.newsagreggator.commands

import com.example.newsagreggator.repository.UserPreferencesRepository
import javax.inject.Inject

class ToggleFollowedCategoryCommand @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : Command<ToggleFollowedCategoryCommand.Input, Set<Int>>() {
    override suspend fun invoke(input: Input): Set<Int> {
        val followedCategories = toggle(input)
        userPreferencesRepository.setFollowedCategories(followedCategories)
        return followedCategories
    }

    fun toggle(input: Input): Set<Int> =
        if (input.category in input.followedCategories) {
            input.followedCategories - input.category
        } else {
            input.followedCategories + input.category
        }

    data class Input(
        val followedCategories: Set<Int>,
        val category: Int,
    )
}
