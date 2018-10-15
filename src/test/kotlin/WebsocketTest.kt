import ch.hsr.dsl.dwrtc.signaling.ClientService
import ch.hsr.dsl.dwrtc.signaling.WebsocketIdMessage
import ch.hsr.dsl.dwrtc.util.findFreePort
import ch.hsr.dsl.dwrtc.websocket.WEBSOCKET_PATH
import ch.hsr.dsl.dwrtc.websocket.WebsocketHandler
import io.javalin.Javalin
import io.javalin.json.JavalinJackson
import io.kotlintest.TestCaseOrder
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import mu.KLogging
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri

class WebsocketTest : WordSpec(), TestListener {
    companion object : KLogging()

    override fun testCaseOrder() = TestCaseOrder.Random // make sure tests are not dependent on each other

    val port = findFreePort()
    val app = Javalin.create().start(port)
    val service = ClientService()
    val wsUri = Uri.of("ws://localhost:$port$WEBSOCKET_PATH")


    private inline fun <reified OutputType> jsonTo(jsonString: String) =
        JavalinJackson.fromJson(jsonString, OutputType::class.java)

    init {
        "the initial message" should {
            WebsocketHandler(app, service)
            "be accessible under the specified port" {
                val client = WebsocketClient.blocking(wsUri)
                val firstMessageString = client.received().take(1).toList().first().bodyString()
                firstMessageString.shouldNotBeBlank()
            }

            "be a WebsocketIdMessage" {
                val client = WebsocketClient.blocking(wsUri)
                val firstMessageString = client.received().take(1).toList().first().bodyString()
                firstMessageString.shouldContain("WebsocketIdMessage")
                firstMessageString.shouldContain("id")
            }

            "have a correct id" {
                val client = WebsocketClient.blocking(wsUri)
                val firstMessageString = client.received().take(1).toList().first().bodyString()
                val firstMessage = jsonTo<WebsocketIdMessage>(firstMessageString)
                firstMessage.id.length.shouldBe(36)
            }

            "have the correct type" {
                val client = WebsocketClient.blocking(wsUri)
                val firstMessageString = client.received().take(1).toList().first().bodyString()
                val firstMessage = jsonTo<WebsocketIdMessage>(firstMessageString)
                firstMessage.type.shouldBe("WebsocketIdMessage")
            }
        }
    }
}
