#include <iostream>
#include <memory>
#include <fstream>
#include "src/services/ApplicationConfig.h"
#include "src/networking/Server.h"
#include "src/services/FileStorageService.h"

using namespace std;

int main(int argc, char* argv[]) {
    try {
        // Check command line arguments
        if (argc < 2) {
            return -1;
        }
        // Parse input number
        int port = stoi(argv[1]);
        
        string configLine;
        // try to read configuration if added to command line
        if (argc >2) {
            for (int i = 2; i < argc; i++) {
                if (i >2) {
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
        // check port bounds
        if (port < 1024 || port > 49151) {
            return -1;
        }
        // Create and start server
        Server server(port, appService);

        if (!server.start()) {
            return -1;
        }
        return 0;
    }
    catch (const exception& e) {
        return -1;
    }
}
