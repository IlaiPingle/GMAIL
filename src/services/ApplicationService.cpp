#include "ApplicationService.h"
#include "../commands/CommandFactory.h"
#include <sstream>

ApplicationService::ApplicationService(
    shared_ptr<IFilterService> filterService,
    shared_ptr<IStorageService> storageService,
    shared_ptr<CommandProcessor> commandProcessor) 
    : m_filterService(filterService),
    m_storageService(storageService),
    m_commandProcessor(commandProcessor){}
    
    bool ApplicationService::initialize(const string& configLine) {
        // Initialize the filter service
        return m_filterService->initialize();
    }
    
    