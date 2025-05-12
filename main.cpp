#include <iostream>
#include <memory>
#include <fstream>
#include "src/services/ApplicationConfig.h"
#include "src/networking/TCPSocketListener.h"
#include "src/networking/Server.h"

int main(int argc, char* argv[]) {
    try {
        // Check command line arguments
        if (argc < 3) {
            std::cerr << "Usage: " << argv[0] << "<ip_address> <port> [config_file]" << std::endl;
            std::cerr << "If config_file is not provided, configuration is read from stdin" << std::endl;
            return 1;
        }

        // Parse port number
        std::string ipAddress = argv[1];
        int port = std::stoi(argv[2]);
        
        // Read initial configuration
        std::string configLine;
        
        if (argc >= 4) {
            // Read configuration from file
            std::ifstream configFile(argv[3]);
            if (!configFile) {
                std::cerr << "Failed to open config file: " << argv[3] << std::endl;
                return 1;
            }
            std::getline(configFile, configLine);
        } else {
            // Read configuration from stdin
            std::getline(std::cin, configLine);
        }
        
        // Configure application
        auto appService = ApplicationConfig::configure(configLine);
        if (!appService) {
            std::cerr << "Failed to configure application" << std::endl;
            return 1;
        }
        
        // Create and start server
        std::shared_ptr<ISocketListener> listener = std::make_shared<TCPSocketListener>();
        Server server(listener, appService);
        
        if (!server.start(port, ipAddress)) {
            return 1;
        }
        
        // Run server (blocks until server is stopped)
        server.run();
        
        return 0;
    }
    catch (const std::exception& e) {
        return 1;
    }
}