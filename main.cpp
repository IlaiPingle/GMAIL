#include <iostream>
#include <memory>
#include <fstream>
#include "src/services/ApplicationConfig.h"
#include "src/networking/TCPSocketListener.h"
#include "src/networking/Server.h"
#include "src/services/FileStorageService.h"

using namespace std;

int main(int argc, char* argv[]) {
    try {
        // Check command line arguments
        if (argc < 3) {
            return -1;
        }
        
        // Parse input number
        string ipAddress = argv[1];
        int port = stoi(argv[2]);
        
        string configLine;
        // try to read configuration if added to command line
        if (argc >3) {
            for (int i = 3; i < argc; i++) {
                if (i >3) {
                    configLine += " ";
                }
                configLine += argv[i];
            }
        }else {
            return -1;
        }
        
        // Configure application
        auto appService = ApplicationConfig::configure(configLine);
        if (!appService) {
            return -1;
        }
        // Create and start server
        shared_ptr<ISocketListener> listener = make_shared<TCPSocketListener>();
        Server server(listener, appService);
        
        if (!server.start(port, ipAddress)) {
            return -1;
        }
        
        // Run server (blocks until server is stopped)
        server.run();
        return 0;
    }
    catch (const exception& e) {
        return -1;
    }
}
