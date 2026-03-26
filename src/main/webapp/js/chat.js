document.addEventListener('DOMContentLoaded', function() {

    var chatBtn = document.getElementById('chat-launcher');
    var chatWindow = document.getElementById('chat-window');
    var chatInput = document.getElementById('chat-input');
    var chatBody = document.getElementById('chat-body');
    var sendBtn = document.getElementById('chat-send');
    
    // Variable to hold the typing element
    var typingBubble = null;

    if (!chatBtn || !chatWindow) return;

    // Toggle Window
    chatBtn.addEventListener('click', function() {
        if (chatWindow.style.display === 'flex') {
            chatWindow.style.display = 'none';
        } else {
            chatWindow.style.display = 'flex';
            if (chatInput) chatInput.focus();
        }
    });

    function sendMessage() {
        var text = chatInput.value.trim();
        if (!text) return;

        // 1. Add User Message
        addBubble(text, 'user');
        chatInput.value = '';
        
        // 2. Show Typing Animation
        showTyping();

        var targetUrl = (typeof chatApiUrl !== 'undefined') ? chatApiUrl : 'ChatServlet';

        fetch(targetUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: text })
        })
        .then(function(res) {
            if (!res.ok) throw new Error("HTTP Status: " + res.status);
            return res.json();
        })
        .then(function(data) {
            // 3. Remove Typing & Add Response
            hideTyping();
            var formattedReply = formatText(data.reply);
            addBubble(formattedReply, 'ai');
        })
        .catch(function(err) {
            hideTyping();
            console.error("Chat Error:", err);
            addBubble("⚠️ Connection failed. (Error: " + err.message + ")", 'ai');
        });
    }

    // --- DYNAMIC TYPING FUNCTIONS ---
    
    function showTyping() {
        // Prevent double bubbles
        if (typingBubble) return; 

        typingBubble = document.createElement('div');
        typingBubble.classList.add('msg-typing');
        typingBubble.innerHTML = '<div class="dot"></div><div class="dot"></div><div class="dot"></div>';
        
        chatBody.appendChild(typingBubble);
        chatBody.scrollTop = chatBody.scrollHeight;
    }

    function hideTyping() {
        if (typingBubble) {
            typingBubble.remove();
            typingBubble = null;
        }
    }

    // --- HELPERS ---

    function addBubble(htmlContent, type) {
        var div = document.createElement('div');
        div.classList.add('msg');
        div.classList.add(type === 'user' ? 'msg-user' : 'msg-ai');
        div.innerHTML = htmlContent;
        chatBody.appendChild(div);
        chatBody.scrollTop = chatBody.scrollHeight;
    }

    function formatText(text) {
        if (!text) return "";
        var formatted = text.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>'); 
        formatted = formatted.replace(/^\* (.*$)/gim, '<li>$1</li>');
        formatted = formatted.replace(/\n/g, '<br>');
        return formatted;
    }

    if (sendBtn) {
        sendBtn.addEventListener('click', sendMessage);
    }
    if (chatInput) {
        chatInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') sendMessage();
        });
    }

});