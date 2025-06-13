FROM ubuntu:22.04

# Install necessary packages
RUN apt-get update && \
apt-get install -y \ 
build-essential \
cmake \
&& rm -rf /var/lib/apt/lists/*


# Set working directory
WORKDIR /app

# Copy entire project for simplicity 
COPY . .
 
# Create build directory
RUN rm -rf /app/build
RUN mkdir -p /app/build
WORKDIR /app/build

RUN cmake .. && make

# run the application
WORKDIR /app/build
CMD ["./bloom_filter_app"]