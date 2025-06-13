#include "Server.h"

Server :: Server( int port, shared_ptr<ApplicationService>  appService) :
m_port(port), m_appService(appService){};

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
            request.clear(); 
        }
    }
    memset(buffer, 0, sizeof(buffer)); // Clear the buffer
    close(clientId); // Close the client socket
}