#ifndef IAPPLICATIONSERVICE_H
#define IAPPLICATIONSERVICE_H
#include <string>
using namespace std;
class IApplicationService {
public:
    virtual ~IApplicationService() = default;
    virtual bool initialize(const string& configLine) = 0;
    virtual string processCommand(const string& commandLine) = 0;
};
#endif // IAPPLICATIONSERVICE_H