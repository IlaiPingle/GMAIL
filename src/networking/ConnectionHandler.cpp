#include "ConnectionHandler.h"
#include "TCPSocketConnection.h"
#include <iostream>

ConnectionHandler::ConnectionHandler(std::shared_ptr<IApplicationService> appService)
    : m_appService(appService) {}

void ConnectionHandler::handleConnection(std::shared_ptr<ISocketConnection> connection) {
    if (!connection || !connection->isConnected()) {
        return;
    }
    
    sendWelcomeMessage(connection);
    
    auto tcpConnection = std::dynamic_pointer_cast<TCPSocketConnection>(connection);
    std::string command;
    
    while (connection->isConnected()) {
        if (tcpConnection) {
            if (tcpConnection->receiveLine(command)) {
                if (command.empty()) continue;
                
                if (command == "exit" || command == "quit") {
                    tcpConnection->sendLine("Goodbye!");
                    break;
                }
                
                if (!processCommand(connection, command)) {
                    break;
                }
            } else {
                break; // Connection closed
            }
        } else {
            if (connection->receiveData(command)) {
                if (command.empty()) continue;
                
                if (command == "exit" || command == "quit") {
                    connection->sendData("Goodbye!\r\n");
                    break;
                }
                
                if (!processCommand(connection, command)) {
                    break;
                }
            } else {
                break; // Connection closed
            }
        }
    }
}
/**
void ConnectionHandler::sendWelcomeMessage(std::shared_ptr<ISocketConnection> connection) {
    if (!connection) return;
    
    auto tcpConnection = std::dynamic_pointer_cast<TCPSocketConnection>(connection);
    if (tcpConnection) {
        tcpConnection->sendLine("--- Welcome to URL Bloom Filter Server ---");
        tcpConnection->sendLine("Enter commands: add/check/delete <url>");
        tcpConnection->sendLine("Type 'exit' or 'quit' to disconnect");
        tcpConnection->sendLine("");
    } else {
        connection->sendData("Welcome to URL Bloom Filter Server\r\n");
        connection->sendData("Enter commands: add/check/delete <url>\r\n");
        connection->sendData("Type 'exit' or 'quit' to disconnect\r\n\r\n");
    }
}**/

bool ConnectionHandler::processCommand(std::shared_ptr<ISocketConnection> connection, const std::string& command) {
    if (!connection || !m_appService) return false;
    
    try {
        std::string response = m_appService->processCommand(command);
        
        auto tcpConnection = std::dynamic_pointer_cast<TCPSocketConnection>(connection);
        if (tcpConnection) {
            tcpConnection->sendLine(response);
        } else {
            connection->sendData(response + "\r\n");
        }
        
        return true;
    } catch (const std::exception& e) {
        std::cerr << "Error processing command: " << e.what() << std::endl;
        
        auto tcpConnection = std::dynamic_pointer_cast<TCPSocketConnection>(connection);
        if (tcpConnection) {
            tcpConnection->sendLine("Error: " + std::string(e.what()));
        } else {
            connection->sendData("Error: " + std::string(e.what()) + "\r\n");
        }
        
        return true;  // Continue processing commands despite the error
    }
}