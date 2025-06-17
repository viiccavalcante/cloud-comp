package com.harbourspace.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.github.cdimascio.dotenv.dotenv


@SpringBootApplication
class ClientApplication

fun main(args: Array<String>) {
    val dotenv = dotenv()

    System.setProperty("DB_USER", dotenv["DB_USER"])
    System.setProperty("DB_PASSWORD", dotenv["DB_PASSWORD"])

    runApplication<ClientApplication>(*args)
}
