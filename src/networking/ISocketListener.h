#ifndef ISOCKETLISTENER_H
#define ISOCKETLISTENER_H
#include <memory>
#include "ISocketConnection.h"

class ISocketListener {
    public:
        virtual ~ISocketListener() = default; // Destructor
        virtual bool start(int port) = 0; // Method to start listening for incoming connections
        virtual bool start(int port, const std::string& ipAddress= "0.0.0.0") = 0; // Method to start listening on a specific IP address
        virtual void stop() = 0; // Method to stop listening for incoming connections
        virtual std::shared_ptr<ISocketConnection> acceptConnection() = 0; // Method to accept a new connection
        virtual bool isListening() const = 0; // Method to check if the listener is currently listening for connections
};
#endif // ISOCKETLISTENER_H