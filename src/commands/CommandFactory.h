#ifndef COMMAND_FACTORY_H
#define COMMAND_FACTORY_H
#include <memory>
#include <unordered_map>
#include "../interfaces/ICommand.h"
#include "../interfaces/ICommandCreator.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"

using namespace std;

class CommandFactory {
private:
    unordered_map<string, shared_ptr<ICommandCreator>> m_commandCreators;
    shared_ptr<IFilterService> m_filterService;
    shared_ptr<IStorageService> m_storageService;
    shared_ptr<IURLValidator> m_urlValidator;

public:
    CommandFactory(shared_ptr<IFilterService> filterService,
                  shared_ptr<IStorageService> storageService,
                  shared_ptr<IURLValidator> urlValidator);
                  
    void registerCommand(const string& name, shared_ptr<ICommandCreator> creator);
    void registerDefaultCommands();
    shared_ptr<ICommand> getCommand(const string& commandName);
    
    static shared_ptr<CommandFactory> createDefault(
        shared_ptr<IFilterService> filterService,
        shared_ptr<IStorageService> storageService,
        shared_ptr<IURLValidator> urlValidator);
};
#endif // COMMAND_FACTORY_H