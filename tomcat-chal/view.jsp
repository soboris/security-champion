<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View Policy</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>View Policy</h1>

        <%
            // Get the requestedPage parameter from the request
            String requestedPage = request.getParameter("page");

            // If requestedPage parameter is empty or null, redirect to view.jsp?page=files/blank.txt
            if (requestedPage == null || requestedPage.trim().isEmpty()) {
                response.sendRedirect("view.jsp?page=files/blank.txt");
                return; // Stop further execution of the page after redirect
            }
        %>

        <jsp:include page="<%= requestedPage %>" flush="true" />

    </div>
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>