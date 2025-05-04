# Use an official Node.js runtime as a parent image
FROM node:14

# Set the working directory to /app
WORKDIR /app

# Copy package.json and package-lock.json if available
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code, including server.js
COPY . .

# Make the 'public' directory
RUN mkdir -p public

# Add a test file to the public directory
RUN echo "This is a test file" > public/test.txt

# Expose port 3000
EXPOSE 3000

# Run server.js when the container launches
CMD ["node", "server.js"]