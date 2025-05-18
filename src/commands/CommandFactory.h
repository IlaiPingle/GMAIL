#ifndef COMMAND_FACTORY_H
#define COMMAND_FACTORY_H
#include <memory>
#include <unordered_map>
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
# include "../commands/AddCommand.h"
#include "../commands/DeleteCommand.h"
#include "../commands/CheckCommand.h"

using namespace std;

class CommandFactory {
private:
    unordered_map<string, shared_ptr<ICommand>> m_commands;
public:
    CommandFactory(const shared_ptr<IFilterService>& filterService);    
    void registerCommand(const string& name, const shared_ptr<ICommand>& command);
    void registerDefaultCommands(const shared_ptr<IFilterService>& filterService);
    shared_ptr<ICommand> getCommand(const string& commandName);
    
};
#endif // COMMAND_FACTORY_H