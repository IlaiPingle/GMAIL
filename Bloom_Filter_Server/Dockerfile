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

# Expose the port on which the application will run
EXPOSE 4000
# run the application
WORKDIR /app/build
CMD ["./bloom_filter_app", "4000" , "1000", "1","2", "3", "4", "5", "6", "7", "8", "9", "10","11", "12", "13", "14", "15", "16", "17", "18", "19", "20"]