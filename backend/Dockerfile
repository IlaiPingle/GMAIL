FROM node:20-alpine

# for bcrypt
RUN apk add --no-cache python3 make g++

# Creating new library for the application
WORKDIR /app
ENV NODE_ENV=development

# Copying the package.json and package-lock.json files
COPY ../package*.json ./

# Installing the dependencies
RUN npm install

# copying application source code
COPY . .

# Exposing the port on which the application will run
EXPOSE 8080

# running the application
CMD ["node", "src/server.js"]