#include "ApplicationService.h"
#include "../commands/CommandFactory.h"
#include <sstream>

ApplicationService::ApplicationService(
    shared_ptr<IFilterService> filterService,
    shared_ptr<IStorageService> storageService,
    shared_ptr<IURLValidator> urlValidator
) : m_filterService(filterService),
    m_storageService(storageService),
    m_urlValidator(urlValidator) {
    
    // Initialize command factory with the URL validator
    m_commandFactory = make_shared<CommandFactory>(
        m_filterService, 
        m_storageService,
        m_urlValidator  // Pass the validator to commands
    );
}

bool ApplicationService::initialize(const string& configLine) {
    // Initialize the filter service
    return m_filterService->initialize();
}

string ApplicationService::processCommand(const string& commandLine) {
    istringstream iss(commandLine);
    string commandName;
    
    if (commandLine.empty()) {
        return "400 Bad Request:\n";
    }
    
    if (!(iss >> commandName)) {
        return "400 Bad Request:\n";
    }
    
    // Get the command object
    auto command = m_commandFactory->getCommand(commandName);
    if (!command) {
        return "400 Bad Request:\n";
    }
    
    // Extract arguments and trim leading whitespace
    string args;
    getline(iss >> ws, args);
    
    // Execute the command
    return command->execute(args);
}