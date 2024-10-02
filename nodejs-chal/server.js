const express = require('express');
const fs = require('fs');
const path = require('path');
const app = express();

app.get('/', (req, res) => {
    // Display "Hello World" when no file is specified
    if (!req.query.file) {
        res.send('Hello World');
        return;
    }

    // If a file is specified, handle it accordingly
    let filePath = req.query.file;
    let fullPath = path.join(__dirname, filePath);

    // Read the file and send its contents as plain text
    fs.readFile(fullPath, 'utf8', (err, data) => {
        if (err) {
            res.status(404).send('File not found');
        } else {
            res.set('Content-Type', 'text/plain');
            res.send(data);
        }
    });
});

app.listen(3000, () => {
    console.log('Server is running on port 3000');
});
