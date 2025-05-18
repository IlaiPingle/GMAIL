#include "../ioHandling/inputManager.h"
#include <iostream>
bool InputManager::splitRequest(string& command, string& url) {
    // Validate command format
    if (command.empty()) {
        return false;
    }
    istringstream iss(command);
    string commandType;
    iss >> commandType;
    
    getline(iss >> ws, url);
    if (!URLValidator::isValidURL(url)) {
        cout << "Invalid URL: " << url << endl; // Debugging line ****
        return false; // Invalid URL
    }
    command = commandType;
    return true;
}
    

