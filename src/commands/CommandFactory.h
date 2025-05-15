#ifndef COMMAND_FACTORY_H
#define COMMAND_FACTORY_H
#include <memory>
#include <unordered_map>
#include "../interfaces/ICommand.h"
#include "../interfaces/IStorageService.h"
#include "../interfaces/IFilterService.h"
# include "../Commands/AddCommand.h"
#include "../Commands/DeleteCommand.h"
#include "../Commands/CheckCommand.h"

using namespace std;

class CommandFactory {
private:
    unordered_map<string, shared_ptr<ICommand>> m_commands;
public:
    CommandFactory(shared_ptr<IFilterService> filterService);
                  
    void registerCommand(const string& name, shared_ptr<ICommand> creator);
    void registerDefaultCommands(const shared_ptr<IFilterService>& filterService);
    shared_ptr<ICommand> getCommand(const string& commandName);
    
    static shared_ptr<CommandFactory> createDefault(
        shared_ptr<IFilterService> filterService,
        shared_ptr<IStorageService> storageService);
};
#endif // COMMAND_FACTORY_H