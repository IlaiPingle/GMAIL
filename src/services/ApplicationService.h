#ifndef APPLICATIONSERVICE_H
#define APPLICATIONSERVICE_H

#include <memory>
#include <string>
#include "../interfaces/IApplicationService.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "CommandProcessor.h"
using namespace std;
/** 
 * @class ApplicationService
 * @brief A class that implements the IApplicationService interface.
 *
 * This class provides methods to initialize the application service 
 */
class ApplicationService : public IApplicationService {
private:
    shared_ptr<IFilterService> m_filterService;
    shared_ptr<IStorageService> m_storageService;
    shared_ptr<CommandProcessor> m_commandProcessor;

public:
    // Constructor 
    // Initializes the application service with filter, storage, and URL validator services
    ApplicationService(
        shared_ptr<IFilterService> filterService,
        shared_ptr<IStorageService> storageService,
        shared_ptr<CommandProcessor> m_commandProcessor);
    

    // initializes the the Bloomfilterservice with configuration line.   
    bool initialize(const string& configLine) override;

    // processes the command line and returns the result
    string processCommand(const string& commandLine) override;

};
#endif // APPLICATIONSERVICE_H