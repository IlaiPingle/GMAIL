#include "TCPSocketConnection.h"
#include <iostream>
#ifdef _WIN32   
    #include <winsock2.h>
    #include <ws2tcpip.h>
    #pragma comment(lib, "Ws2_32.lib")
    #define SOCKET_CLOSE closesocket
    using SocketType = SOCKET;
#else
    #include <unistd.h>
    #include <sys/socket.h>
    #include <sys/types.h>
    #define SOCKET_CLOSE ::close
    #define INVALID_SOCKET -1
    #define SOCKET_ERROR -1
    using SocketType = int;
#endif

TCPSocketConnection::TCPSocketConnection(int socket)
    : m_socket(socket), m_isConnected(socket != INVALID_SOCKET) {}

TCPSocketConnection::~TCPSocketConnection() {
    close();
}

bool TCPSocketConnection::isConnected() const {
    return m_isConnected;
}

bool TCPSocketConnection::sendData(const std::string& data) {
    if (!m_isConnected) {
        return false;
    }
    // Track the total number of bytes sent
    size_t totalSent = 0;
    size_t dataSize = data.length();
    while (totalSent < dataSize) {
        int bytesSent = send(m_socket, data.c_str() + totalSent, dataSize - totalSent, 0);
        if (bytesSent <= 0) {
            m_isConnected = false; // Connection closed or error occurred
            return false;
        }
        totalSent += bytesSent;
    }
    return true; // All data sent successfully
}

bool TCPSocketConnection::receiveData(std::string& data) {
    if (!m_isConnected) {
        return false;
    }
    char buffer[BUFFER_SIZE];
    int bytesReceived = recv(m_socket, buffer, BUFFER_SIZE - 1, 0);
    if (bytesReceived > 0) {
        buffer[bytesReceived] = '\0'; // Null-terminate the received data
        data = buffer;
        return true;
    } else if (bytesReceived == 0) {
        m_isConnected = false; // Connection closed by the peer
        return false;
    } else{
        m_isConnected = false; // Error occurred
        std::cerr << "Error receiving data: " << errno << std::endl;
        return false;
    }
}

bool TCPSocketConnection::sendLine(const std::string& line) {
    std::string dataWithNewline = line;
    if (dataWithNewline.empty() || (dataWithNewline.back() != '\n' && 
        (dataWithNewline.length() < 2 || dataWithNewline.substr(dataWithNewline.length() - 2) != "\r\n"))) {
        dataWithNewline += "\r\n";
    }
    return sendData(dataWithNewline);
}

bool TCPSocketConnection::receiveLine(std::string& line) {
    static std::string buffer;
    // Check if we have a complete line in the buffer
    size_t pos = buffer.find("\r\n");
    if (pos != std::string::npos) {
        // Extract the line
        line = buffer.substr(0, pos);
        buffer = buffer.substr(pos + 2);  // Remove the line and \r\n from buffer
        return true;
    }
    // Otherwise, receive more data
    std::string data;
    if (!receiveData(data)) {
        return false;  // Connection closed or error
    }
    buffer += data;
    // Now check again for a complete line
    pos = buffer.find("\r\n");
    if (pos != std::string::npos) {
        line = buffer.substr(0, pos);
        buffer = buffer.substr(pos + 2);
        return true;
    }
    // If we don't have a complete line yet, return what we have (partial line)
    if (!buffer.empty()) {
        line = buffer;
        buffer.clear();
        return true;
    }
    return false;  // No data available
}

void TCPSocketConnection::close() {
    if (m_isConnected) {
        SOCKET_CLOSE(m_socket);
        m_isConnected = false;
    }
}