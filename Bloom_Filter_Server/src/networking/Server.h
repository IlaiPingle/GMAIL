#ifndef SERVER_H
#define SERVER_H
#include <memory>
#include <string>
#include <string.h>
#include <iostream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
# include "../services/ApplicationService.h"


using namespace std;
class Server {
    private:
        int m_port;
        shared_ptr<ApplicationService> m_appService;
    public: 
        Server(int port,shared_ptr<ApplicationService> appService);
        bool start();
        void handleClient(int clientId);
};
#endif // SERVER_H