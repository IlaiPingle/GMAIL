FROM ubuntu:22.04

# Install necessary packages
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    g++ \
    git \
    make \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy entire project for simplicity 
COPY . .

# Create a symlink to handle the server.h capitalization issue
RUN mkdir -p networking && \
    ln -sf /app/src/networking/Server.h /app/networking/server.h && \
    ln -sf /app/src/networking/Server.cpp /app/networking/server.cpp

# Create build directory
RUN mkdir -p build

# Debug: Show what files are where
RUN ls -la && \
    ls -la src/networking && \
    ls -la Tests

# Build with detailed output
WORKDIR /app/build
RUN cmake .. -DCMAKE_VERBOSE_MAKEFILE=ON && cmake --build .

# Run only the server tests using Google Test's filter option
CMD ["./runTests", "--gtest_filter=ServerTest.*"]