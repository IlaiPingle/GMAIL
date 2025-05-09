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
    ApplicationService(
        shared_ptr<IFilterService> filterService,
        shared_ptr<IStorageService> storageService,
        shared_ptr<IURLValidator> urlValidator);
    bool initialize(const string& configLine) override;
    string processCommand(const string& commandLine) override;
};
#endif // APPLICATIONSERVICE_H