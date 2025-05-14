#ifndef TCPSOCKETLISTENER_H
#define TCPSOCKETLISTENER_H
#include "ISocketListener.h"
#include <memory>
using namespace std;
class TCPSocketListener : public ISocketListener {
    private:
        int m_socketServer; // Socket for listening to incoming connections
        bool m_isListening; // Flag to indicate if the listener is currently listening
        bool m_hasAcceptedConnection; // Flag to indicate if a connection has been accepted
    public:
        TCPSocketListener(); // Constructor
        ~TCPSocketListener(); // Destructor
        bool start(int port) override; // Method to start listening for incoming connections
        bool start(int port, const string& ipAddress = "0.0.0.0") override;
        void stop() override; 
        shared_ptr<ISocketConnection> acceptConnection() override;
        bool isListening() const override;
        bool hasAcceptedConnection() const; // Method to check if a connection has been accepted 
};
#endif // TCPSOCKETLISTENER_H
