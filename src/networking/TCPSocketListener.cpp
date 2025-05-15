#include "TCPSocketConnection.h"
#include "TCPSocketListener.h"
#include <iostream>


#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#define SOCKET_CLOSE ::close
#define INVALID_SOCKET -1
#define SOCKET_ERROR -1
using SocketType = int;

TCPSocketListener::TCPSocketListener()
: m_socketServer(INVALID_SOCKET), m_isListening(false), m_hasAcceptedConnection(false) {
}

TCPSocketListener::~TCPSocketListener() {
    stop();
}

bool TCPSocketListener::start(int port, const string& ipAddress) {
    m_hasAcceptedConnection = false;
    if (m_isListening) {
        return false; // Already listening
    }
    m_socketServer = socket(AF_INET, SOCK_STREAM, 0);
    if (m_socketServer == INVALID_SOCKET) {
        return false;
    }
    int opt = 1;
    if (setsockopt(m_socketServer, SOL_SOCKET, SO_REUSEADDR, (const char*)&opt, sizeof(opt)) < 0) {
        SOCKET_CLOSE(m_socketServer);
        return false;
    }
    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    if (ipAddress == "0.0.0.0") {
        serverAddr.sin_addr.s_addr = INADDR_ANY; // Listen on all interfaces
    } else {
        serverAddr.sin_addr.s_addr = inet_addr(ipAddress.c_str());
    }
    serverAddr.sin_port = htons(port);
    
    if (bind(m_socketServer, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        SOCKET_CLOSE(m_socketServer);
        return false;
    }
    
    if (listen(m_socketServer, 1) == SOCKET_ERROR) {
        SOCKET_CLOSE(m_socketServer);
        return false;
    }
    
    m_isListening = true;
    return true;
}

shared_ptr<ISocketConnection> TCPSocketListener::acceptConnection() {
    if (!m_isListening || m_hasAcceptedConnection) {
        return nullptr; // Not listening
    }
    struct sockaddr_in clientAddr;
    socklen_t clientAddrLen = sizeof(clientAddr);
    int clientSocket = accept(m_socketServer, (struct sockaddr*)&clientAddr, &clientAddrLen);
    if (clientSocket == INVALID_SOCKET) {
        return nullptr;
    }
    m_hasAcceptedConnection = true;
    return make_shared<TCPSocketConnection>(clientSocket);
}

void TCPSocketListener::stop() {
    if (m_isListening) {
        SOCKET_CLOSE(m_socketServer);
        m_isListening = false;
        m_hasAcceptedConnection = false;
    }
}

bool TCPSocketListener::isListening() const {
    return m_isListening;
}

bool TCPSocketListener::hasAcceptedConnection() const {
    return m_hasAcceptedConnection;
}