document.addEventListener('DOMContentLoaded', function() {
    const authLinks = document.getElementById('authLinks');
    const userMgmtLink = document.getElementById('userMgmtLink');
    const editProfileLink = document.getElementById('editProfileLink');
    const ticketLink = document.getElementById('ticketLink'); 
    const noticeLink = document.getElementById('noticeLink'); 
    const token = localStorage.getItem('jwtToken');

    // Hide the Edit Profile, User Management, Tickets, and Notice links by default
    editProfileLink.style.display = 'none';
    userMgmtLink.style.display = 'none';
    ticketLink.style.display = 'none';
    noticeLink.style.display = 'none';

    // Function to decode the JWT token
    function parseJwt(token) {
        try {
            const base64Url = token.split('.')[1];
            const base64 = decodeURIComponent(atob(base64Url).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(base64);
        } catch (e) {
            console.error('Failed to decode JWT token');
            return null;
        }
    }

    // Function to set a cookie
    function setCookie(name, value, days) {
        const d = new Date();
        d.setTime(d.getTime() + (days * 24 * 60 * 60 * 1000));
        const expires = "expires=" + d.toUTCString();
        document.cookie = name + "=" + value + ";" + expires + ";path=/";
    }

    if (token) {
        const decodedToken = parseJwt(token);
        // console.log('Decoded JWT:', decodedToken);

        const username = decodedToken ? decodedToken.username : null;
        const userId = decodedToken ? decodedToken.userId : null;  // Extract the userId from the token
        const role = decodedToken ? decodedToken.role : null;

        // Check if the user is an admin, and show the User Management link if true
        if (role === 0) {
            userMgmtLink.style.display = 'block';

            // Generate flag (username|role|@admin) and base64 encode it
            const flagData = `${username}|${role}|@admin`;
            const encodedFlag = btoa(flagData);

            // Set flag in a cookie
            setCookie('flag', encodedFlag, 1); // Save for 1 day
        }

        if (username && userId) {
            // Show the Edit Profile and Tickets links
            editProfileLink.style.display = 'block';
            editProfileLink.querySelector('a').href = `/pages/protected/edituser?userId=${userId}`;

            ticketLink.style.display = 'block';  // Show "Tickets" link
            noticeLink.style.display = 'block';  // Show "Notice" link

            // Add username and logout link to authLinks
            authLinks.innerHTML = `
                <li class="nav-item">
                    <span class="nav-link">Welcome, ${username}!</span>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" id="logoutButton">Logout</a>
                </li>
            `;

            // Logout functionality
            document.getElementById('logoutButton').addEventListener('click', function(event) {
                event.preventDefault();
                if (confirm('Are you sure you want to log out?')) {
                    localStorage.removeItem('jwtToken');
                    alert('You have been logged out.');
                    window.location.href = '/pages/login';
                }
            });
        }
    } else {
        // Show login and register links if no token exists
        authLinks.innerHTML = `
            <li class="nav-item">
                <a class="nav-link" href="/pages/login">Login</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="/pages/register">Register</a>
            </li>
        `;
    }
});
