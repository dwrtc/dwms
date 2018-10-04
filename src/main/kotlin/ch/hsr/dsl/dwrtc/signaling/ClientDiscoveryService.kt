package ch.hsr.dsl.dwrtc.signaling

import net.tomp2p.dht.PeerBuilderDHT
import net.tomp2p.p2p.PeerBuilder
import net.tomp2p.peers.Number160
import net.tomp2p.peers.PeerAddress
import java.util.*

class ClientDiscoveryService() {
    private val peerId = UUID.randomUUID().toString()
    private val peer = PeerBuilderDHT(PeerBuilder(Number160.createHash(peerId)).ports(4000).start()).start()!!

    constructor(bootstrapPeerAddress: PeerConnectionDetails) : this() {
        peer.peer().bootstrap().inetAddress(bootstrapPeerAddress.ip_address).ports(bootstrapPeerAddress.port).start()
            .awaitListeners()
    }

    fun registerClient(sessionId: String) {
        peer.put(Number160.createHash(sessionId)).`object`(peer.peerAddress()).start().awaitUninterruptibly()
    }

    fun deregisterClient(sessionId: String) {
        peer.remove(Number160.createHash(sessionId)).all().start().awaitUninterruptibly()
    }

    fun findClient(sessionId: String): Client {
        val peerIdGet = peer.get(Number160.createHash(sessionId)).start().awaitUninterruptibly()
        return if (peerIdGet.isSuccess) {
            val peerAddress = peerIdGet.data().`object`() as PeerAddress
            Client(peer, sessionId, peerAddress)
        } else throw Exception("No peer found under session ID $sessionId")
    }
}
