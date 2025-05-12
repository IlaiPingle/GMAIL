#ifndef CONNECTION_HANDLER_H
#define CONNECTION_HANDLER_H

#include <memory>
#include "ISocketConnection.h"
#include "../interfaces/IApplicationService.h"

class ConnectionHandler {
private:
    std::shared_ptr<IApplicationService> m_appService;
    
public:
    ConnectionHandler(std::shared_ptr<IApplicationService> appService);
    void handleConnection(std::shared_ptr<ISocketConnection> connection);
    void sendWelcomeMessage(std::shared_ptr<ISocketConnection> connection);
    bool processCommand(std::shared_ptr<ISocketConnection> connection, const std::string& command);
};

#endif // CONNECTION_HANDLER_H