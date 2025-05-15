#include "../ioHandling/inputManager.h"






    

bool InputManager::splitRequest(string& command, string& url) {
    // Validate command format
    if (command.empty()) {
        return false;
    }
    istringstream iss(command);
    string commandType;
    iss >> commandType;
    
    getline(iss >> ws, url);

    string standardURL = URLValidator::standardize(url);
    if (standardURL.empty()) {
        return false;
    }
    url = standardURL;
    command = commandType;
    return true;
}
    

