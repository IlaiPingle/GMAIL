#ifndef IAPPLICATIONSERVICE_H
#define IAPPLICATIONSERVICE_H
#include <string>
using namespace std;
/** 
 * @brief Interface for the application service.
 * This interface defines the basic operations for initializing the application
 * and processing commands. It serves as a contract for any application service
 * implementation, ensuring that the necessary methods are provided.
 */
class IApplicationService {
public:
    virtual ~IApplicationService() = default;
    virtual bool initialize(const string& configLine) = 0;
    virtual string processCommand(const string& commandLine) = 0;
};
#endif // IAPPLICATIONSERVICE_H