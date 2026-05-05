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

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function (frame) {
        appendMessage('Connected to the chat server.', 'system');

        // Subscribe to Public channel
        stompClient.subscribe('/topic/public', function (payload) {
            const message = JSON.parse(payload.body);
            appendMessage(`[Public] ${message.sender}: ${message.content}`, 'public');
        });

        // Subscribe to Private channel specific to this user
        stompClient.subscribe('/user/queue/private', function (payload) {
            const message = JSON.parse(payload.body);
            appendMessage(`[Private from ${message.sender}]: ${message.content}`, 'private');
        });
    });
}

function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const recipientInput = document.getElementById('recipient');
    
    const content = messageInput.value.trim();
    const recipient = recipientInput.value;

    if (content && stompClient) {
        const chatMessage = {
            content: content,
            recipient: recipient
        };

        if (recipient === "") {
            // Send Public
            stompClient.send("/app/chat.public", {}, JSON.stringify(chatMessage));
        } else {
            // Send Private
            stompClient.send("/app/chat.private", {}, JSON.stringify(chatMessage));
            // Manually append our own outgoing private message to the UI
            appendMessage(`[Private to ${recipient}]: ${content}`, 'private');
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
}

// Allow pressing "Enter" to send
document.getElementById("message-input").addEventListener("keypress", function(event) {
    if (event.key === "Enter") {
        event.preventDefault();
        sendMessage();
    }
});