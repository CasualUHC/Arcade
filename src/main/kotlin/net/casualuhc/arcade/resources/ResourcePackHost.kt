package net.casualuhc.arcade.resources

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.math.BigInteger
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.file.Path
import java.security.MessageDigest
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.io.path.*
import kotlin.random.Random

open class ResourcePackHost(
    private val packs: Path,
    private val threads: Int = 3
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val hosted = HashMap<String, HostedPack>()
    private val builder = ThreadFactoryBuilder().setNameFormat("Pack-Host-%d").build()
    private val executor = Executors.newSingleThreadExecutor(this.builder)

    private var server: HttpServer? = null
    private var pool: ExecutorService? = null

    fun getHostedPack(name: String): HostedPack? {
        val zipped = if (name.endsWith(".zip")) name else "$name.zip"
        return this.hosted[zipped]
    }

    fun start(hostIp: String? = null, hostPort: Int = 24464, randomise: Boolean = false) {
        this.executor.execute {
            val restart = this.server !== null
            this.hosted.clear()
            this.server?.stop(0)
            this.pool?.shutdownNow()
            try {
                this.logger.info("${if (restart) "Restarting" else "Starting"} ResourcePackHost...")
                var ip = hostIp
                if (ip === null) {
                    this.logger.info("No IP address found for ResourcePackHost, using localhost instead")
                    ip = InetAddress.getLocalHost().hostAddress
                }

                val server = HttpServer.create(InetSocketAddress("0.0.0.0", hostPort), 0)
                this.pool = Executors.newFixedThreadPool(this.threads, this.builder)
                server.executor = this.pool

                val packs = this.packs.listDirectoryEntries("*.zip")
                for (pack in packs) {
                    val sub = if (randomise) Random.nextInt(Int.MAX_VALUE).toString() else pack.fileName

                    val url = "http://${ip}:${hostPort}/${sub}"
                    val hash = "%040x".format(
                        BigInteger(1, MessageDigest.getInstance("SHA-1").digest(pack.readBytes()))
                    )
                    val hosted = HostedPack(pack, url, hash)
                    this.hosted[pack.name] = hosted

                    server.createContext("/$sub", Handler(hosted))

                    this.logger.info("Hosting pack: ${pack.name}")
                }

                server.start()
                val hosting = hosted.values.joinToString("\n") { "${it.path.name}: ${it.url}" }
                this.logger.info("ResourcePackHost successfully started, hosted:\n${hosting}")
            } catch (e: Exception) {
                this.logger.error("Failed to start the ResourcePackHost!", e)
            }
        }
    }

    fun shutdown() {
        this.server?.stop(0)
        this.pool?.shutdownNow()
        this.executor.shutdownNow()
    }

    class HostedPack(val path: Path, val url: String, val hash: String)

    private inner class Handler(val hosted: HostedPack): HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if ("GET" == exchange.requestMethod && this.hosted.path.exists()) {
                val username = exchange.requestHeaders.getFirst("X-Minecraft-Username")
                if (username != null) {
                    logger.info("Player $username requested pack")
                } else {
                    logger.info("Non-player requested pack")
                }
                val file = this.hosted.path.toFile()
                exchange.responseHeaders.add("User-Agent", "Java/ResourcePackHost")
                exchange.sendResponseHeaders(200, file.length())
                exchange.responseBody.use { response ->
                    FileInputStream(file).use { fileStream ->
                        BufferedInputStream(fileStream).use { buffered ->
                            buffered.transferTo(response)
                        }
                    }
                }
            } else {
                exchange.sendResponseHeaders(400, 0)
            }
        }
    }
}