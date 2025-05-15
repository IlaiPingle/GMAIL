#include "Server.h"
#include "TCPSocketListener.h"
#include <iostream>

// Test constructor
// Server::Server(int port) 
// : m_port(port), 
// m_initialized(port > 0), // Simple validation for test
// m_running(false),
// m_socketListener(make_shared<TCPSocketListener>()),
// m_appService(nullptr) {
// }

Server :: Server( int port, shared_ptr<IApplicationService>  appService) :
m_port(port), m_appService(appService){};

// // Production constructor
// Server::Server(shared_ptr<ISocketListener> socketListener,
//     shared_ptr<IApplicationService> appService)
//     : m_port(0),  // Default port, will be set when starting
//     m_initialized(socketListener != nullptr && appService != nullptr),
//     m_running(false),
//     m_socketListener(socketListener),
//     m_appService(appService) {
//     }
    
//     Server::~Server() {
//         stop();
//     }
    
//     int Server::getPort() const {
//         return m_port;
//     }
    
//     bool Server::isInitialized() const {
//         return m_initialized;
//     }
    
//     bool Server::start(int port) {
//         if (!m_initialized) {
//             return false;
//         }
        
//         if (m_running) {
//             return true; // Already running
//         }
        
//         m_port = port;
//         string ipAddress = "127.0.0.1";
//         bool started = false;
//         if (m_socketListener) {
//             started = m_socketListener->start(port, ipAddress);
//         }
        
//         if (started) {
//             m_running = true;
//         }
        
//         return m_running;
//     }
    
//     bool Server::isRunning() const {
//         return m_running;
//     }
    
//     void Server::stop() {
//         if (m_running && m_socketListener) {
//             m_socketListener->stop();
//             m_running = false;
//         }
//     }
    
//     void Server::run() {
//         if (!m_initialized || !m_socketListener) {
//             return; // Not initialized or no socket listener
//         }
//         m_running = true;
//         while (m_running) {
//             try {
//                 auto connection = m_socketListener->acceptConnection();
//                 if (!connection || !connection->isConnected()) {
//                     continue; // No valid connection
//                 }
//                 bool clientConnected = true;
//                 while (clientConnected && m_running) {
//                     string data;
//                     if (connection->receiveData(data)) {
//                         // Process the received data
//                         if (m_appService) {
//                             string response = m_appService->processCommand(data);
//                             connection->sendData(response + "\n");
//                         }
//                     } else {
//                         clientConnected = false; // Connection closed or error
//                     }
//                 }
//             }
//             catch (const exception& e) {
//             }
//         }
//     }
//     // Test helper method
//     bool Server::handleClient(int clientId) {
//         if (!m_running) {
//             return false;
//         }
        
//         // Simple validation for test cases
//         return clientId >= 0;
//     }
    
    bool Server::start() {
        int server_fd, client_socket;
        struct sockaddr_in address;
        int addrlen = sizeof(address);
        
        
        if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
            return false;
        }
        address.sin_family = AF_INET;
        address.sin_addr.s_addr = INADDR_ANY;
        address.sin_port = htons(m_port);
        
        if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
            return false;
        }
        
        if (listen(server_fd, 1024) < 0) {
            return false;
        }
        while (true) {
            client_socket = accept(server_fd, (struct sockaddr*)&address, (socklen_t*)&addrlen);
            if (client_socket < 0 ) {
                continue;
            }
            handleClient(client_socket);
        }
        close(server_fd);
        return true;
    }
    void Server::handleClient(int clientId) {
        char buffer[1024]={0};
        string request="";
        while(true){
            ssize_t bytesRead= recv(clientId, buffer, sizeof(buffer) - 1, 0);
            if (bytesRead <= 0) {
                break; // Connection closed or error
            }
            buffer[bytesRead] = '\0'; // Null-terminate the string
            request += buffer;
            if(request.back() == '\n') {
                string response = m_appService->processCommand(request);
                send(clientId, response.c_str(), response.size(), 0);
            }
            
        }
        memset(buffer, 0, sizeof(buffer)); // Clear the buffer
        close(clientId); // Close the client socket
    }