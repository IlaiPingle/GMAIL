#ifndef IAPPLICATIONSERVICE_H
#define IAPPLICATIONSERVICE_H
#include <string>

class IApplicationService {
public:
    virtual ~IApplicationService() = default;
    virtual bool initialize(const std::string& configLine) = 0;
    virtual std::string processCommand(const std::string& commandLine) = 0;
};
#endif // IAPPLICATIONSERVICE_H