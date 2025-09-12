#!/bin/bash

# Create Mortgage Loan Dashboard Project Structure
echo "Creating Mortgage Loan Dashboard project structure..."

# Create main project directory
mkdir -p mortgage-loan-dashboard
cd mortgage-loan-dashboard

# Create backend structure
echo "Creating backend structure..."
mkdir -p backend/src/main/java/com/lender/mortgage/{config,controller,entity/enums,repository,service,dto/{request,response},security}
mkdir -p backend/src/main/resources
mkdir -p backend/src/test

# Create frontend structure
echo "Creating frontend structure..."
mkdir -p frontend/public
mkdir -p frontend/src/{components/{common,auth,dashboard,loans,conditions,documents},services,hooks,utils,context}

# Create database structure
mkdir -p database

# Create configuration files directories
touch backend/pom.xml
touch backend/Dockerfile
touch backend/src/main/resources/application.yml
touch backend/src/main/resources/data.sql

touch frontend/package.json
touch frontend/vite.config.js
touch frontend/tailwind.config.js
touch frontend/Dockerfile
touch frontend/public/index.html
touch frontend/src/main.jsx
touch frontend/src/App.jsx
touch frontend/src/index.css

touch database/schema.sql
touch database/sample-data.sql
touch database/stored-procedures.sql
touch docker-compose.yml

echo "Project structure created successfully!"
echo ""
echo "Next steps:"
echo "1. Navigate to mortgage-loan-dashboard directory"
echo "2. Follow the file creation guides for backend and frontend"
echo "3. Set up your database using the SQL files"
echo "4. Configure environment variables"
echo "5. Run docker-compose up or start services individually"

# Display the created structure
echo ""
echo "Created structure:"
find . -type d | sort