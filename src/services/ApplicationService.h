#ifndef APPLICATIONSERVICE_H
#define APPLICATIONSERVICE_H

#include <memory>
#include <string>
#include "../interfaces/IApplicationService.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IURLValidator.h"
#include "../commands/CommandFactory.h"

using namespace std;

class ApplicationService : public IApplicationService {
private:
    shared_ptr<IFilterService> m_filterService;
    shared_ptr<IStorageService> m_storageService;
    shared_ptr<IURLValidator> m_urlValidator;
    shared_ptr<CommandFactory> m_commandFactory;

public:
    // Constructor 
    // Initializes the application service with filter, storage, and URL validator services
    ApplicationService(
        shared_ptr<IFilterService> filterService,
        shared_ptr<IStorageService> storageService,
        shared_ptr<IURLValidator> urlValidator);
    
    // initializes the the Bloomfilterservice with configuration line.   
    bool initialize(const string& configLine) override;
    // Processes a command line input and returns the result as a string.
    string processCommand(const string& commandLine) override;
};
#endif // APPLICATIONSERVICE_H