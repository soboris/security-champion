<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.Part" %>
<%@ page import="java.io.File" %>
<%@ page import="java.nio.file.Paths" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Upload Demo</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>File Upload</h1>
        <p>Upload a file and view the uploaded file's link below.</p>

        <!-- File upload form -->
        <form action="fileupload.jsp" method="post" enctype="multipart/form-data" class="mt-4">
            <div class="form-group">
                <label for="fileUpload">Choose file:</label>
                <input type="file" class="form-control-file" id="fileUpload" name="file">
            </div>
            <button type="submit" class="btn btn-primary">Upload</button>
        </form>

        <!-- Display uploaded file information -->
        <%
            String filePath = "";
            if (request.getMethod().equalsIgnoreCase("POST")) {
                try {
                    // File upload logic
                    String uploadPath = application.getRealPath("") + "files/";
                    File uploadDir = new File(uploadPath);
                    
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir(); // Create uploads directory if it doesn't exist
                    }

                    // Get the uploaded file
                    Part filePart = request.getPart("file"); // Fetch the uploaded file
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // Extract the file name
                    
                    // Save the uploaded file
                    filePart.write(uploadPath + fileName);
                    filePath = "files/" + fileName;
                } catch (Exception e) {
                    out.println("<div class='alert alert-danger mt-4'>File upload failed: " + e.getMessage() + "</div>");
                }
            }
        %>

        <%
            if (!filePath.isEmpty()) {
        %>
        <div class="alert alert-success mt-4">
            <p>File uploaded successfully!</p>
            <p>File path: <a href="<%= filePath %>" target="_blank"><%= filePath %></a></p>
        </div>
        <%
            }
        %>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
