#include "ConnectionHandler.h"
#include "TCPSocketConnection.h"
#include <iostream>

ConnectionHandler::ConnectionHandler(shared_ptr<IApplicationService> appService)
: m_appService(appService) {}

void ConnectionHandler::handleConnection(shared_ptr<ISocketConnection> connection) {
    if (!connection || !connection->isConnected()) {
        return;
    }
    
    auto tcpConnection = dynamic_pointer_cast<TCPSocketConnection>(connection);
    string command;
    
    while (connection->isConnected()) {
        bool gotCommand = false;
        if (tcpConnection) {
            gotCommand = tcpConnection->receiveLine(command);
        } else {
            gotCommand = connection->receiveData(command);
        }
        
        if (!gotCommand) {
            break; // Connection closed
        }
        
        if (command.empty()) continue;
        
        try {
            processCommand(connection, command);
        } catch (...) {
            
        }
    }
}

bool ConnectionHandler::processCommand(shared_ptr<ISocketConnection> connection, const string& command) {
    if (!connection || !m_appService) return false;
    
    try {
        string response = m_appService->processCommand(command);
        
        auto tcpConnection = dynamic_pointer_cast<TCPSocketConnection>(connection);
        if (tcpConnection) {
            tcpConnection->sendLine(response);
        } else {
            connection->sendData(response + "\r\n");
        }
        return true;
    } catch (...) {
        return false; // Handle any exceptions;
    }
}