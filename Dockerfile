FROM gcc:latest
RUN apt-get update && apt-get install -y cmake
COPY . /usr/src
WORKDIR /usr/src
RUN mkdir build
WORKDIR /usr/src/build
RUN cmake .. && make
CMD ["./runTests"]