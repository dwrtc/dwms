package ch.hsr.dsl.dwrtc.websocket

import ch.hsr.dsl.dwrtc.signaling.ClientService
import ch.hsr.dsl.dwrtc.signaling.PeerConnectionDetails
import ch.hsr.dsl.dwrtc.util.BOOTSTRAP_IP
import ch.hsr.dsl.dwrtc.util.BOOTSTRAP_PORT
import ch.hsr.dsl.dwrtc.util.WEBSERVER_PORT
import ch.hsr.dsl.dwrtc.util.config
import com.natpryce.konfig.Misconfiguration
import io.javalin.Javalin
import io.javalin.staticfiles.Location

fun main(args: Array<String>) {
    val app = Javalin.create()
            .enableStaticFiles("src/main/resources/public/", Location.EXTERNAL)
            // or .enableStaticFiles("/public") to use Class Path
            .start(config[WEBSERVER_PORT])

    val service = try {
        val bootstrapIp = config[BOOTSTRAP_IP]
        val bootstrapPort = config[BOOTSTRAP_PORT]
        val peerConnectionDetails = PeerConnectionDetails(bootstrapIp, bootstrapPort)
        ClientService(peerConnectionDetails)
    } catch (e: Misconfiguration) {
        ClientService()
    }


    WebsocketHandler(app, service)
}
