#include "CommandFactory.h"
#include "AddCommandCreator.h"
#include "CheckCommandCreator.h"
#include "DeleteCommandCreator.h"

CommandFactory::CommandFactory(shared_ptr<IFilterService> filterService, 
                              shared_ptr<IStorageService> storageService, 
                              shared_ptr<IURLValidator> urlValidator) 
    : m_filterService(filterService), m_storageService(storageService), m_urlValidator(urlValidator) {
    registerDefaultCommands();
}

void CommandFactory::registerCommand(const string& name, shared_ptr<ICommandCreator> creator) {
    m_commandCreators[name] = creator;
}

void CommandFactory::registerDefaultCommands() {
    registerCommand("add", make_shared<AddCommandCreator>(m_filterService, m_urlValidator));
    registerCommand("check", make_shared<CheckCommandCreator>(m_filterService, m_urlValidator));
    registerCommand("delete", make_shared<DeleteCommandCreator>(m_filterService, m_urlValidator));
}

shared_ptr<ICommand> CommandFactory::getCommand(const string& commandName) {
    auto it = m_commandCreators.find(commandName);
    if (it != m_commandCreators.end()) {
        return it->second->createCommand();
    }
    return nullptr;
}

shared_ptr<CommandFactory> CommandFactory::createDefault(
    shared_ptr<IFilterService> filterService,
    shared_ptr<IStorageService> storageService,
    shared_ptr<IURLValidator> urlValidator) {
    
    return make_shared<CommandFactory>(filterService, storageService, urlValidator);
}