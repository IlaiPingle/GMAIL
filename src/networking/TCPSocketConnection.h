#ifndef TCPSOCKETCONNECTION_H
#define TCPSOCKETCONNECTION_H
#include "ISocketConnection.h"

class TCPSocketConnection : public ISocketConnection {
    private:
        int m_socket; // Socket file descriptor
        bool m_isConnected; // Connection status
        static const int BUFFER_SIZE = 1024; // Buffer size for data transfer
    public:
        TCPSocketConnection(int socket);
        ~TCPSocketConnection() override;

        bool isConnected() const override;
        bool sendData(const std::string& data) override;
        bool receiveData(std::string& data) override;
        bool sendLine(const std::string& line);
        bool receiveLine(std::string& line);
        void close() override;
};
#endif // TCPSOCKETCONNECTION_H