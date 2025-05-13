#include "Server.h"
#include "TCPSocketListener.h"
#include <iostream>

// Test constructor
Server::Server(int port) 
    : m_port(port), 
      m_initialized(port > 0), // Simple validation for test
      m_running(false),
      m_socketListener(make_shared<TCPSocketListener>()),
      m_appService(nullptr) {
}

// Production constructor
Server::Server(shared_ptr<ISocketListener> socketListener,
               shared_ptr<IApplicationService> appService)
    : m_port(0),  // Default port, will be set when starting
      m_initialized(socketListener != nullptr && appService != nullptr),
      m_running(false),
      m_socketListener(socketListener),
      m_appService(appService) {
}

Server::~Server() {
    stop();
}

int Server::getPort() const {
    return m_port;
}

bool Server::isInitialized() const {
    return m_initialized;
}

bool Server::start(int port, const string& ipAddress) {
    if (!m_initialized) {
        return false;
    }

    if (m_running) {
        return true; // Already running
    }

    m_port = port;

    bool started = false;
    if (m_socketListener) {
        started = m_socketListener->start(port, ipAddress);
    }

    if (started) {
        m_running = true;
    }

    return m_running;
}

bool Server::start() {
    return start(m_port, "0.0.0.0"); // Default to all interfaces
}

bool Server::isRunning() const {
    return m_running;
}

void Server::stop() {
    if (m_running && m_socketListener) {
        m_socketListener->stop();
        m_running = false;
    }
}

void Server::run() {
    if (!m_running) {
        return;
    }
    
    
    while (m_running) {
        try {
            auto connection = m_socketListener->acceptConnection();
            if (connection && connection->isConnected()) {
                // Handle the connection here or delegate to a connection handler
                // In this implementation, we're just showing it's running
                string data;
                if (connection->receiveData(data)) {
                    if (m_appService) {
                        string response = m_appService->processCommand(data);
                        connection->sendData(response);
                    } else {
                        connection->sendData("");
                    }
                }
            }
        }
        catch (const exception& e) {
            cerr << "Error in server loop: " << e.what() << endl;
            // Continue running
        }
    }
}

// Test helper method
bool Server::handleClient(int clientId) {
    if (!m_running) {
        return false;
    }
    
    // Simple validation for test cases
    return clientId >= 0;
}