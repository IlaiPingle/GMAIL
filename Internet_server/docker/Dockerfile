FROM node:20

# Creating new library for the application
WORKDIR /app

# Copying the package.json and package-lock.json files
COPY ../package*.json ./

# downloading dependencies
RUN npm install

# copying the rest of the application files
COPY ../src ./src

# Exposing the port on which the application will run
EXPOSE 8080

# Setting environment variables
ENV NODE_ENV=production
ENV PORT=8080

# running the application
CMD ["node", "src/server.js"]