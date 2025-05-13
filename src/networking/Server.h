#ifndef SERVER_H
#define SERVER_H
#include <memory>
#include <string>
#include "ISocketListener.h"
#include "ConnectionHandler.h"
#include "../interfaces/IApplicationService.h"

class Server {
    private:
        int m_port;
        bool m_running;
        bool m_initialized;
        std::shared_ptr<ISocketListener> m_socketListener;
        //std::shared_ptr<ConnectionHandler> m_connectionHandler;
        std::shared_ptr<IApplicationService> m_appService;
        

    public:
        Server(int port);
        Server(std::shared_ptr<ISocketListener> socketListener,
               std::shared_ptr<IApplicationService> appService);
        ~Server();
        int getPort() const;
        bool isRunning() const;
        bool isInitialized() const;
        bool handleClient(int clientId);
        bool start();
        bool start(int port, const std::string& ipAddress);
        void stop();
        void run();
};
#endif // SERVER_H