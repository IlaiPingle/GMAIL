#ifndef TCPSOCKETLISTENER_H
#define TCPSOCKETLISTENER_H
#include "ISocketListener.h"
#include <memory>

class TCPSocketListener : public ISocketListener {
    private:
        int m_socketServer; // Socket for listening to incoming connections
        bool m_isListening; // Flag to indicate if the listener is currently listening
        bool m_hasAcceptedConnection; // Flag to indicate if a connection has been accepted
    public:
        TCPSocketListener(); // Constructor
        ~TCPSocketListener(); // Destructor
        bool start(int port, const std::string& ipAddress = "0.0.0.0") override;
        void stop() override; 
        std::shared_ptr<ISocketConnection> acceptConnection() override;
        bool isListening() const override;
        bool hasAcceptedConnection() const; // Method to check if a connection has been accepted 
};
#endif // TCPSOCKETLISTENER_H
