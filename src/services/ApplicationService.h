#ifndef APPLICATIONSERVICE_H
#define APPLICATIONSERVICE_H

#include <memory>
#include <string>
#include "../interfaces/IApplicationService.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IURLValidator.h"
#include "../commands/CommandFactory.h"

class ApplicationService : public IApplicationService {
private:
    std::shared_ptr<IFilterService> m_filterService;
    std::shared_ptr<IStorageService> m_storageService;
    std::shared_ptr<IURLValidator> m_urlValidator;
    std::shared_ptr<CommandFactory> m_commandFactory;

public:
    ApplicationService(
        std::shared_ptr<IFilterService> filterService,
        std::shared_ptr<IStorageService> storageService,
        std::shared_ptr<IURLValidator> urlValidator);
    bool initialize(const std::string& configLine) override;
    std::string processCommand(const std::string& commandLine) override;
};
#endif // APPLICATIONSERVICE_H