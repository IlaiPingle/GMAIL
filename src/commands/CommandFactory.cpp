#include "CommandFactory.h"
#include "AddCommand.h"
#include "CheckCommand.h"
//#include "DeleteCommand.h"
using namespace std;

CommandFactory::CommandFactory(shared_ptr<IFilterService> filterService, shared_ptr<IStorageService> storageService, 
                                shared_ptr<IURLValidator> urlValidator) 
            : m_filterService(filterService), m_storageService(storageService), m_urlValidator(urlValidator) {    
    // Register commands
    registerDefaultCommands();
}

void CommandFactory::registerDefaultCommands() {
    registerCommand("add", make_shared<AddCommand>(m_filterService, m_urlValidator));
    registerCommand("check", make_shared<CheckCommand>(m_filterService, m_urlValidator));
    //registerCommand("delete", make_shared<DeleteCommand>(m_filterService, m_urlValidator));
}

void CommandFactory::registerCommand(const string &name, shared_ptr<ICommand> command) {
    m_commands[name] = command;
}

shared_ptr<ICommand> CommandFactory::getCommand(const string &commandName) {
    auto it = m_commands.find(commandName);
        if (it != m_commands.end()) {
            return it->second;
        }
        return nullptr;
}
