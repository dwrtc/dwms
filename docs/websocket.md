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

## API Doc
