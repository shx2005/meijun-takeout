const uuid = "uuid";

const ws = new WebSocket("ws://localhost:8080/ws/"+uuid);
// Note: Use "ws://" or "wss://" for WebSocket connections, not "http://"

ws.onopen = function () {
    console.log("Connected to WebSocket");
    ws.send("Hello Server");
};

ws.onmessage = function (event) {
    console.log("Received from server:", event.data);
};

ws.onclose = function () {
    console.log("Connection closed");
};

ws.onerror = function (error) {
    console.error("WebSocket Error:", error);
};
