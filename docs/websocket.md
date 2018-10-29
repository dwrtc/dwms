# Package ch.hsr.dsl.dwrtc.websocket

UI layer. Handles sending and receiving messages from the WebSocket connections.

## Design Decisions

### Message format

We're re-using the [Message] format for our WebSocket messages.

### Few Components

The [WebSocketHandler] is relatively small. It just consists of four components:

* [WebSocketHandler.clients] is a map of session ID to [InternalClient]s
* [WebSocketHandler.sessions] is map of session ID to WebSocket sessions
* [WebSocketHandler.onReceiveMessageFromWebSocket] uses the session ID to get the [InternalClient]. This is then used to send a message to send a message through the P2P layer
* [WebSocketHandler.onReceiveMessageFromSignaling] uses the session ID to get the WebSocket session. This is then used to send the message to the specific WebSocket

The rest of the class are just some handlers that register the specific WebSocket callbacks to those methods.

## API Doc

This API is quite simple.

When connected to the WebSocket you can send or receive the following message types:

### Send

* `SignalingMessage`. Send your black box signaling message to another peer.
  * `recipientSessionId: String`. The recipient's session ID. Used for routing.
  * `messageBody: String`. The free-form message body. To the transport layer, this is a black box.
  
### Receive

You must handle all these incoming message types. They are distinguishable by their `type` field.
``
* `WebSocketIdMessage`. Tells you your session ID.
  * `type: String`. Static value `WebSocketIdMessage`
  * `id: String`. Your session ID
* `WebSocketErrorMessage`. Tells you that something went wrong
  * `type: String`. Static value `WebSocketErrorMessage`
  * `error: String`. The error message
* `SignalingMessage`. Incoming, signaling messages
  * `type: String`. Static value `SignalingMessage`
  * `senderSessionId: String`. The sender' session ID. Can be used to reply to messages
  * `recipientSessionId: String`. The recipient's session ID. This should be your session ID!
  * `messageBody: String`. The free-form message body
