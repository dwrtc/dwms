package ch.hsr.dsl.dwrtc.signaling

import net.tomp2p.dht.PeerDHT
import net.tomp2p.peers.PeerAddress

class Client(private val peer: PeerDHT, val sessionId: String, val peerAddress: PeerAddress) {
    fun sendMessage(messageBody: String, recipient: Client) {
        peer.peer().sendDirect(recipient.peerAddress).`object`(MessageDto(sessionId, messageBody)).start()
    }

    fun onReceiveMessage(emitter: (MessageDto) -> Any) {
        peer.peer().objectDataReply { sender, request -> if (request is MessageDto) emitter(request) }
    }
}
