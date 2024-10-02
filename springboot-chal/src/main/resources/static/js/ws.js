document.addEventListener('DOMContentLoaded', function () {
    // Get references to the Ticket Info bar and message elements
    const ticketInfoBar = document.getElementById('ticketInfoBar');
    const ticketInfoMessage = document.getElementById('ticketInfoMessage');
    
    // Check if the user is logged in (using token from localStorage)
    const token = localStorage.getItem('jwtToken');

    // Only proceed if the user is logged in
    if (token) {
        // Establish a WebSocket connection using STOMP
        const socket = new SockJS('/ws'); // Using SockJS for WebSocket fallback
        const stompClient = Stomp.over(socket);

        // Connect to the WebSocket server
        stompClient.connect({}, function (frame) {
            console.log('Connected to WebSocket: ' + frame);

            // Subscribe to the ticket updates
            stompClient.subscribe('/topic/tickets', function (message) {
                const ticketInfo = message.body;
                console.log('Ticket update received: ', ticketInfo);

                // Update the Ticket Info bar with the new message
                ticketInfoMessage.textContent = ticketInfo;
            });
        });

        // Error handling for WebSocket connection
        stompClient.onStompError = function (error) {
            console.error('WebSocket error: ', error);
        };
    } else {
        console.log('User not logged in. WebSocket connection not established.');
    }
});
