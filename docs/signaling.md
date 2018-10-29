# Package ch.hsr.dsl.dwrtc.signaling

P2P layer. Handles sending and receiving messages through TomP2P.

You need an instance of [ClientService]. Through this, you will do all your operations.

Specifically, you can [add a new client][ClientService.addClient]. This will get you an [InternalClient]. You then add a message listener to this client, which will relay the messages you receive via the TomP2P layer.

To connect to other clients, you [find clients][ClientService.findClient]. This will get you an [ExternalClient]. You can then use an InternalClient to [send messages][InternalClient.sendMessage] to this ExternalClient. (This will call the message listener on the other peer's InternalClient you're sending messages to.)

## Design decisions

### Anti-cheating mechanisms

As we're in a P2P network, it is possible to add bad data. However, we wanted to make this a bit harder, so we at least don't expose it on our API.

* [SignalingMessage.recipientSessionId] and [SignalingMessage.senderSessionId] IDs are overwritten before they go to the TomP2P layer, since they could be faked by WebSocket clients
* [ClientService.removeClient] only accepts an [IInternalClient] (that you got from [ClientService.addClient]), so you cannot easily disconnect another user.

### Messaging Format

The [Message] class uses a `type` as it's discriminator. Every other field depends on the type. This way, it's a very flexible format that's also easy to cast in a type-safe way.

### Bootstrapping

The [ClientService] class has two bootstrapping constructors: one for bootstrapping with a given TomP2P `PeerAddress`, one with a normal IP/port pair.

* The `PeerAddress` bootstrap mechanism is meant for tests, when you already have the peer's address in the correct, technical format
* The IP/port pair bootstrap mechanism is meant for user input

### ClientService

The [ClientService] class is meant as the one-stop starting point for all DHT operations. It's meant as a singleton that creates and bootstraps the TomP2P peer. All objects are created through methods of this class.

### Futures

When we decided to build a nicer API for TomP2P Futures, we split it up.

In this layer, you can find the high-level [Future]s. These are a rework of the API that TomP2P gives.

In the [util][ch.hsr.dsl.dwrtc.util] layer, you can find the extension classes that build right on top of TomP2P. Our [Future] classes build on these.

### InternalClient/ExternalClient

We decided to make a distinction between those.

An [InternalClient] is created when we receive a new WebSocket session. It is able to send and receive messages. An [ExternalClient] is created when an [InternalClient] wants to send messages to it. It mainly consists of the found peer address this client is found at. An [ExternalClient] can only receive messages. On the other peer, the messages are then routed to a corresponding [InternalClient].

Note that, of course, an [ExternalClient] can be on the same server. This just makes handling things a lot nicer, since you don't have to care about such distinctions.

 
### Interfaces

On all these classes, we defined an Interface first before we did the actual implementation. This allows us to, in theory, swap the TomP2P layer underlying it. The interface is meant to be agnostic to the P2P layer it stands on.

### Message Routing

When an [InternalClient] is created, the [ClientService] registers its session ID in a message dispatcher table. When the TomP2P peer receives a message, this dispatcher table is used to send the message to the correct [InternalClient] 
