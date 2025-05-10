#include "TCPSocketConnection.h"

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
    int bytesSent = send(m_socket, data.c_str(), data.length() + 1, 0);
    return bytesSent > 0;
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
    }
    m_isConnected = false; // Connection closed or error occurred
    return false;
}

void TCPSocketConnection::close() {
    if (m_isConnected) {
        SOCKET_CLOSE(m_socket);
        m_isConnected = false;
    }
}