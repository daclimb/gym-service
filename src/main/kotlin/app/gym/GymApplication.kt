package app.gym

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GymApplication

fun main(args: Array<String>) {
    runApplication<GymApplication>(*args)
}
