#include "CommandProcessor.h"
#include "../utils/URLValidator.h"
#include "../bloom_Filter/bloomFilter.h"
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
        cout << "200 Ok\n" << endl;
        return "false"; // Empty URL check
    }
    if (standardURL.empty()) {
        return "400 Bad Request"; // Invalid URL format
    }
    if (!m_bloomFilter->contains(standardURL)) {
        cout << "200 Ok\n" << endl;
        return "false";
    }
    else if (m_bloomFilter->containsAbsolutely(standardURL)) {
        cout << "200 Ok\n" << endl;
        return "true true";
    } else {
        cout << "200 Ok\n" << endl;
        return "true false";
    }
}

string CommandProcessor::deleteFromBlacklist(const string& url) {
    if (url.empty()) {
        return "404 Not Found"; // Empty URL check
    }
    if (!(m_storageService->isInBlacklist(url))) {
        return "404 Not Found"; // URL not in blacklist
    }
    string standardURL = URLValidator().standardize(url);
    if (standardURL.empty() || !m_storageService->isInBlacklist(standardURL)) {
        return "404 Not Found"; // Invalid URL format or not in blacklist
    }
    
    if (m_storageService->removeFromBlacklist(standardURL)) {
        // Reload the blacklist to reflect changes
        unordered_set<string> updatedBlacklist;
        m_storageService->loadBlacklist(updatedBlacklist);
        m_bloomFilter->setBlackList(updatedBlacklist);
        return "204 No Content"; // Successfully deleted from blacklist
    }
    return "404 Not Found"; // URL not found in blacklist
}