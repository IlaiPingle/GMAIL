#include "CommandProcessor.h"
#include "../utils/URLValidator.h"
#include "../bloom_filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../interfaces/IStorageService.h"
#include <iostream>






/** מתן תעבור על החלק הזה ותגיד אם יש משהו שצריך לשמור פה ולהעביר למקומות הנכונים 
string CommandProcessor::deleteFromBlacklist(const string& url) {
if (url.empty()) {
return "400 Bad Request"; // Empty URL check
}
// Standardize the URL first
URLValidator urlValidator;
string standardURL = urlValidator.standardize(url);
if (standardURL.empty()) {
return "400 Bad Request"; // Invalid URL format
}
// Rest of your existing implementation
bool isRemoved = m_storageService->removeFromBlacklist(standardURL);
m_bloomFilter->remove(standardURL);
string rawURL = url;
if (rawURL.find("http://") == 0) {
rawURL = rawURL.substr(7); // Remove "http://"
m_bloomFilter->remove(rawURL);
m_storageService->removeFromBlacklist(rawURL);
}
unordered_set<string> blackList = m_bloomFilter->getBlackList();
bool wasInList = (blackList.find(standardURL) != blackList.end() || 
blackList.find(rawURL) != blackList.end());
unordered_set<string> updatedBlacklist;
m_storageService->loadBlacklist(updatedBlacklist);
m_bloomFilter->setBlackList(updatedBlacklist);
if (isRemoved || wasInList){
return "204 No Content"; // Successfully deleted from blacklist
} else {
return "404 Not Found"; // URL not found in blacklist
}

}*/
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
