let stompClient = null;
let currentUser = null;

// 1. Fetch the logged-in username, then connect to WebSockets
fetch('/api/username')
        .then(response => response.text())
        .then(username => {
            currentUser = username;
            document.getElementById('username-display').innerText = currentUser;
            connect();
        });

// Add a map to track our outgoing messages
const pendingMessages = new Map();

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        appendMessage('Connected to the chat server.', 'system');

        // NEW: Fetch any messages missed while offline
        fetch('/api/messages/offline')
                .then(response => response.json())
                .then(messages => {
                    if (messages.length > 0) {
                        appendMessage(`--- You have ${messages.length} offline messages ---`, 'system');
                        messages.forEach(msg => {
                            appendMessage(`[Offline from ${msg.sender}]: ${msg.content}`, 'private');
                            acknowledgeDelivery(msg);
                        });
                    }
                });

        // Subscribe to Public channel
        stompClient.subscribe('/topic/public', function (payload) {
            const message = JSON.parse(payload.body);
            appendMessage(`[Public] ${message.sender}: ${message.content}`, 'public');
        });

        // Subscribe to Private channel specific to this user
        stompClient.subscribe('/user/queue/private', function (payload) {
            const message = JSON.parse(payload.body);
            appendMessage(`[Private from ${message.sender}]: ${message.content}`, 'private');

            // acknowledge delivery
            acknowledgeDelivery(message);            
        });

        // NEW: Subscribe to Delivery Receipts
        stompClient.subscribe('/user/queue/receipts', function (payload) {
            const clientMsgId = payload.body;

            // Find the UI element associated with this message and update it
            if (pendingMessages.has(clientMsgId)) {
                const msgElement = pendingMessages.get(clientMsgId);
                msgElement.innerHTML += ' <span style="color: green; font-size: 0.8em;">(Delivered ✓)</span>';
                pendingMessages.delete(clientMsgId); // Cleanup
            }
        });
    });
}

function acknowledgeDelivery(message) {
    // NEW: Tell the server we successfully received this live message
    if (message.id) {
        fetch(`/api/messages/${message.id}/delivered`, {
            method: 'POST'
        });
    }
}

function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const recipientInput = document.getElementById('recipient');

    const content = messageInput.value.trim();
    const recipient = recipientInput.value;

    if (content && stompClient) {
        // Generate a unique ID for this message
        const uniqueId = Math.random().toString(36).substring(2, 15);

        const chatMessage = {
            content: content,
            recipient: recipient,
            clientMessageId: uniqueId
        };

        if (recipient === "") {
            // Send Public
            stompClient.send("/app/chat.public", {}, JSON.stringify(chatMessage));
        } else {
            // Send Private
            stompClient.send("/app/chat.private", {}, JSON.stringify(chatMessage));
            // Manually append our own outgoing private message to the UI
            const elementRef = appendMessage(`[Private to ${recipient}]: ${content}`, 'private');
            pendingMessages.set(uniqueId, elementRef);
        }
        messageInput.value = '';
    }
}

function appendMessage(text, type) {
    const chatBox = document.getElementById('chat-box');
    const messageElement = document.createElement('div');
    messageElement.classList.add('msg', type);
    messageElement.innerText = text;
    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;

    return messageElement;
}

// Allow pressing "Enter" to send
document.getElementById("message-input").addEventListener("keypress", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();
        sendMessage();
    }
});