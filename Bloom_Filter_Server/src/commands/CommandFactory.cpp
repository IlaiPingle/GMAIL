#include "CommandFactory.h"


CommandFactory::CommandFactory(const shared_ptr<IFilterService>& filterService) {
    registerDefaultCommands(filterService);
}

void CommandFactory::registerCommand(const string& name,const shared_ptr<ICommand>& command) {
    m_commands[name] = command;
}

void CommandFactory::registerDefaultCommands(const shared_ptr<IFilterService>& filterService) {
    registerCommand("POST", make_shared<AddCommand>(filterService));
    registerCommand("GET", make_shared<CheckCommand>(filterService));
    registerCommand("DELETE", make_shared<DeleteCommand>(filterService));
}

shared_ptr<ICommand> CommandFactory::getCommand(const string& commandName) {
    auto command = m_commands.find(commandName);
    if (command != m_commands.end()) {
        return command->second;
    }
    return nullptr;
}
