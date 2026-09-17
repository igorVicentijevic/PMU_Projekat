package com.example.newsagreggator.business.command

abstract class Command<in Input, out Output> {
    abstract suspend operator fun invoke(input: Input): Output
}
