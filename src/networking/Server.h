#ifndef SERVER_H
#define SERVER_H
#include <memory>
#include <string>
#include <string.h>
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include "ISocketListener.h"
#include "ConnectionHandler.h"
#include "../interfaces/IApplicationService.h"
using namespace std;
class Server {
    private:
        int m_port;
        shared_ptr<IApplicationService> m_appService;
    public: 
        Server(int port,shared_ptr<IApplicationService> appService);
        bool start();
        void handleClient(int clientId);
        // bool m_running;
        // bool m_initialized;
        // shared_ptr<ISocketListener> m_socketListener;
        //shared_ptr<ConnectionHandler> m_connectionHandler;
        
        

    public:

        Server(shared_ptr<ISocketListener> socketListener,
               shared_ptr<IApplicationService> appService);
        ~Server();
        int getPort() const;
        bool isRunning() const;
        bool isInitialized() const;
        // bool handleClient(int clientId);
        bool start();
        bool start(int port);
        void stop();
        void run();
};
#endif // SERVER_H