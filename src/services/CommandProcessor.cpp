#include "CommandProcessor.h"
#include "../utils/URLValidator.h"
#include "../bloom_filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../interfaces/IStorageService.h"
#include <iostream>

CommandProcessor::CommandProcessor()
    : m_bloomFilter(make_unique<bloomFilter>()),
      m_storageService(make_unique<FileStorageService>()) {}

CommandProcessor::CommandProcessor(unique_ptr<IBloomFilter> bloomFilter,
                                 unique_ptr<IStorageService> storageService)
    : m_bloomFilter(move(bloomFilter)), m_storageService(move(storageService)) {}

string CommandProcessor::addToBlacklist(const string& url) {
    if (url.empty()) {
        return "400 Bad Request"; // Empty URL check
    }
    URLValidator urlValidator;
    string standardURL = urlValidator.standardize(url);
    if (standardURL.empty()) {
        return "400 Bad Request"; // Invalid URL format
    }
    m_bloomFilter->add(standardURL);
    m_storageService->saveBitArray(m_bloomFilter->getBitArray());
    m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
    return "201 Created"; // Successfully added to blacklist
}

string CommandProcessor::checkBlacklist(const string& url) {
    URLValidator urlValidator;
    string standardURL = urlValidator.standardize(url);
    if (url.empty()) {
        return "400 Bad Request"; // Empty URL check
    }
    if (standardURL.empty()) {
        return "400 Bad Request"; // Invalid URL format
    }
    return "200 OK"; // URL is valid
}

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
}
