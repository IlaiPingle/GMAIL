#include "CommandProcessor.h"
#include <iostream>
CommandProcessor::CommandProcessor(const shared_ptr<IFilterService>& filterService)
    : m_commandFactory(filterService) {}
    
string CommandProcessor::ProssessCommand(const string& request) {
    string command = request;
    string url;
    bool isSplited = InputManager::splitRequest(command, url);
    if (!isSplited) {
        return "400\n"; // Invalid command format
    }
    shared_ptr<ICommand> commandObject = m_commandFactory.getCommand(command);
    if (!commandObject) {
        return "400\n"; // Command not found
    }
    // Execute the command and get the response
    string response = commandObject->execute(url);

    if (response ==""){
        return "404\n";
    }
    return response;
}
