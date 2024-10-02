# Use the official MySQL image
FROM mysql:latest

# Copy the initialization SQL script into the MySQL init directory
COPY init-db.sql /docker-entrypoint-initdb.d/
