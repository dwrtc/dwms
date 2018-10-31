# Package ch.hsr.dsl.dwrtc.signaling

P2P layer. Handles sending and receiving messages through TomP2P.

An instance of [ClientService] is needed. All operations are done through this.

Specifically, one can [add a new client][ClientService.addClient]. This will return an [InternalClient]. A message listener can then be added to this client, which will relay the messages the client receives via the TomP2P layer.

To connect to other clients, [find clients][ClientService.findClient]. This will return an [ExternalClient]. An [InternalClient] can then be used to [send messages][InternalClient.sendMessage] to this ExternalClient. (This will call the message listener on the other peer's InternalClient).

## Design decisions

### User Input 

* [SignalingMessage.recipientSessionId] and [SignalingMessage.senderSessionId] IDs are overwritten before they go to the TomP2P layer.
* [ClientService.removeClient] only accepts an [IInternalClient] (that was received from [ClientService.addClient]), so one cannot easily disconnect another user.

### Messaging Format

The [Message] class uses a `type` as it's discriminator. Every other field depends on the type. This way, it's a very flexible format that's also easy to cast in a type-safe way.

### Bootstrapping

The [ClientService] class has two bootstrapping constructors: one for bootstrapping with a given TomP2P `PeerAddress`, one with a normal IP/port pair.

* The `PeerAddress` bootstrap mechanism is meant for tests, when peer's address is already available in the correct, technical format
* The IP/port pair bootstrap mechanism is meant for user input

### ClientService

The [ClientService] class is meant as the one-stop starting point for all DHT operations. It's meant as a singleton that creates and bootstraps the TomP2P peer. All objects are created through methods of this class.

### Futures

This layer contains the high-level [Future]s. These are a rework of the API that TomP2P gives.

The extension classes that build right on top of TomP2P can be found in the the [util][ch.hsr.dsl.dwrtc.util] layer. The [Future] classes build on these.

### InternalClient/ExternalClient

An [InternalClient] is created when a new WebSocket session is started. It is able to send and receive messages. 
An [ExternalClient] is created when an [InternalClient] wants to send messages to it. It mainly consists of the found peer address this client is found at.
An [ExternalClient] can only receive messages. On the other peer, the messages are then routed to a corresponding [InternalClient].

Note: an [ExternalClient] *can* be on the same server.

 
### Interfaces

All classes define an interface. These interfaces allow the substitution of the underlying P2P layer. The interface is meant to be agnostic to the P2P layer.

### Message Routing

When an [InternalClient] is created, the [ClientService] registers its session ID in a message dispatcher table. When the TomP2P peer receives a message, this dispatcher table is used to send the message to the correct [InternalClient] 
