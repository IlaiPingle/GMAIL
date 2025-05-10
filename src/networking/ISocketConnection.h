#ifndef ISOCKETCONNECTION_H
#define ISOCKETCONNECTION_H
#include <string>

class ISocketConnection {
public:
    virtual ~ISocketConnection() = default;
    virtual bool isConnected() const = 0;
    virtual bool sendData(const std::string& data) = 0;
    virtual bool receiveData(std::string& data) = 0;
    virtual void close() = 0;
};
#endif // ISOCKETCONNECTION_H