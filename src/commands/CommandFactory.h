#ifndef COMMAND_FACTORY_H
#define COMMAND_FACTORY_H
#include <memory>
#include <unordered_map>
#include <functional>
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IURLValidator.h"
using namespace std;
class CommandFactory {
private:
    shared_ptr<IFilterService> m_filterService;
    shared_ptr<IStorageService> m_storageService;
    shared_ptr<IURLValidator> m_urlValidator;
    unordered_map<string, shared_ptr<ICommand>> m_commands;
    void registerDefaultCommands(); // Register default commands

public:
    CommandFactory(shared_ptr<IFilterService> filterService, 
                   shared_ptr<IStorageService> storageService, 
                   shared_ptr<IURLValidator> urlValidator);
    shared_ptr<ICommand> getCommand(const string& commandName);
    void registerCommand(const string& name, shared_ptr<ICommand> command);
};
#endif // COMMAND_FACTORY_H