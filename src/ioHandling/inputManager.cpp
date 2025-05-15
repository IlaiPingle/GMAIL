#include "../ioHandling/inputManager.h"
#include "../bloom_filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../bloom_filter/hashFactory.h"

#include <sstream>



    

bool InputManager::splitRequest(string& command, string& url) {
    // Validate command format
    if (command.empty()) {
        return false;
    }
    istringstream iss(command);
    string commandType;
    iss >> commandType;
    
    string url;
    getline(iss >> ws, url);

    string standardURL = URLValidator::standardize(url);
    if (standardURL.empty()) {
        return false;
    }
    url = standardURL;
    command = commandType;
    return true;
}
    

