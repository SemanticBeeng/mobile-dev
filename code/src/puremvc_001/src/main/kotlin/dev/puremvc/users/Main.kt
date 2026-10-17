package dev.puremvc.users

import dev.puremvc.users.data.JsonUserRepository
import dev.puremvc.users.puremvc.UserRegistryApplication
import dev.puremvc.users.ui.KotterUserRegistryScreen
import dev.puremvc.users.ui.PlainUserRegistryScreen
import java.nio.file.Path

fun main(args: Array<String>) {
    val options = AppOptions.parse(args.toList())
    if (options.help) {
        println(AppOptions.usage)
        return
    }

    val screen = if (options.plain) PlainUserRegistryScreen() else KotterUserRegistryScreen()
    UserRegistryApplication(
        repository = JsonUserRepository(options.dataFile),
        screen = screen
    ).use { application ->
        application.launch(seedDemo = options.seedDemo)
    }
}

private data class AppOptions(
    val dataFile: Path,
    val seedDemo: Boolean,
    val plain: Boolean,
    val help: Boolean
) {
    companion object {
        const val usage = """
            PureMVC User Registry

            Options:
              --data-file PATH   JSON store path (default: ./data/users.json)
              --seed-demo        Add demo users only when the store is empty
              --plain            Use line-oriented terminal I/O instead of Kotter
              --help             Show this help
        """

        fun parse(args: List<String>): AppOptions {
            var dataFile = Path.of("data", "users.json")
            var seedDemo = false
            var plain = false
            var help = false
            var index = 0

            while (index < args.size) {
                when (val argument = args[index]) {
                    "--data-file" -> {
                        require(index + 1 < args.size) { "--data-file requires a path.\n$usage" }
                        dataFile = Path.of(args[++index])
                    }
                    "--seed-demo" -> seedDemo = true
                    "--plain" -> plain = true
                    "--help", "-h" -> help = true
                    else -> error("Unknown option: $argument\n$usage")
                }
                index++
            }

            return AppOptions(dataFile, seedDemo, plain, help)
        }
    }
}