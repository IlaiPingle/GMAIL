FROM node:20

# Creating new library for the application
WORKDIR /app

# Copying the package.json and package-lock.json files
COPY ../package*.json ./

# Installing the dependencies
RUN npm install

# copying application source code
COPY ../src ./src
COPY .env ./

# Exposing the port on which the application will run
EXPOSE 8080

# running the application
CMD ["node", "src/server.js"]