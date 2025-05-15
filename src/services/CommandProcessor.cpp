#include "CommandProcessor.h"

CommandProcessor::CommandProcessor(shared_ptr<IFilterService> filterService)
    : m_commandFactory(filterService) {}
    
string CommandProcessor::ProssessCommand(const string& request) {
    string command = request;
    string url;
    bool isSplited = InputManager::splitRequest(command, url);
    if (!isSplited) {
        return "400 Bad Request"; // Invalid command format
    }
    string response;
    if (command == "POST") {
        response = m_commandFactory.getCommand("add")->execute(url);
    }
    else if (command == "GET") {
        response = m_commandFactory.getCommand("check")->execute(url);
    }
    else if (command == "DELETE") {
        response = m_commandFactory.getCommand("delete")->execute(url);
    }
    else {
        return "400 Bad Request\n";
    }
    if (response ==""){
        return "404 Not Found\n";
    }
    return response;
}
