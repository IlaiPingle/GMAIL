#ifndef APPLICATIONSERVICE_H
#define APPLICATIONSERVICE_H

#include <memory>
#include "FileStorageService.h"
#include "CommandProcessor.h"
#include "../interfaces/IFilterService.h"

using namespace std;
/** 
 * @class ApplicationService
 * @brief A class that implements the IApplicationService interface.
 *
 * This class provides methods to initialize the application service 
 */
class ApplicationService {
private:
    shared_ptr<CommandProcessor> m_commandProcessor;

public:
    // Constructor 
    // Initializes the application service with filter, storage, and URL validator services
    ApplicationService(
        shared_ptr<CommandProcessor> m_commandProcessor);
    // processes the command line and returns the result
    string processCommand(const string& commandLine) ;

};
#endif // APPLICATIONSERVICE_H