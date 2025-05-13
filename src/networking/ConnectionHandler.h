#ifndef CONNECTION_HANDLER_H
#define CONNECTION_HANDLER_H

#include <memory>
#include "ISocketConnection.h"
#include "../interfaces/IApplicationService.h"
using namespace std;
class ConnectionHandler {
private:
    shared_ptr<IApplicationService> m_appService;
    
public:
    ConnectionHandler(shared_ptr<IApplicationService> appService);
    void handleConnection(shared_ptr<ISocketConnection> connection);
    void sendWelcomeMessage(shared_ptr<ISocketConnection> connection);
    bool processCommand(shared_ptr<ISocketConnection> connection, const string& command);
};

#endif // CONNECTION_HANDLER_H