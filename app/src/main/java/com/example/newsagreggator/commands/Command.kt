package com.example.newsagreggator.commands

abstract class Command<in Input, out Output> {
    abstract suspend operator fun invoke(input: Input): Output
}