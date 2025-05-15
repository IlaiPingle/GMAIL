#include "CommandFactory.h"


CommandFactory::CommandFactory(shared_ptr<IFilterService> filterService) {
    registerDefaultCommands(filterService);
}

void CommandFactory::registerCommand(const string& name, shared_ptr<ICommand> creator) {
    m_commands[name] = creator;
}

void CommandFactory::registerDefaultCommands(const shared_ptr<IFilterService>& filterService) {
    registerCommand("add", make_shared<AddCommand>(filterService));
    registerCommand("check", make_shared<CheckCommand>(filterService));
    registerCommand("delete", make_shared<DeleteCommand>(filterService));
}

shared_ptr<ICommand> CommandFactory::getCommand(const string& commandName) {
    auto command = m_commands.find(commandName);
    if (command != m_commands.end()) {
        return command->second;
    }
    return nullptr;
}

shared_ptr<CommandFactory> CommandFactory::createDefault(
    shared_ptr<IFilterService> filterService,
    shared_ptr<IStorageService> storageService){
    
    return make_shared<CommandFactory>(filterService, storageService);
}