#include "CommandProcessor.h"
#include "../utils/URLValidator.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../services/FileStorageService.h"
#include "../interfaces/IStorageService.h"
#include <iostream>

CommandProcessor::CommandProcessor()
    : m_bloomFilter(std::make_unique<bloomFilter>()),
      m_storageService(std::make_unique<FileStorageService>()) {}

CommandProcessor::CommandProcessor(std::unique_ptr<IBloomFilter> bloomFilter,
                                 std::unique_ptr<IStorageService> storageService)
    : m_bloomFilter(std::move(bloomFilter)), m_storageService(std::move(storageService)) {}

std::string CommandProcessor::addToBlacklist(const std::string& url) {
    URLValidator urlValidator;
    std::string standardURL = urlValidator.standardize(url);
    if (standardURL.empty()) {
        return "400 Bad Request"; // Invalid URL format
    }
    m_bloomFilter->add(standardURL);
    m_storageService->saveBitArray(m_bloomFilter->getBitArray());
    m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
    return "201 Created"; // Successfully added to blacklist
}

std::string CommandProcessor::checkBlacklist(const std::string& url) {
    URLValidator urlValidator;
    std::string standardURL = urlValidator.standardize(url);
    if (url.empty()) {
        std::cout << "200 Ok\n" << std::endl;
        return "false"; // Empty URL check
    }
    if (standardURL.empty()) {
        return "400 Bad Request"; // Invalid URL format
    }
    if (!m_bloomFilter->contains(standardURL)) {
        std::cout << "200 Ok\n" << std::endl;
        return "false";
    }
    else if (m_bloomFilter->containsAbsolutely(standardURL)) {
        std::cout << "200 Ok\n" << std::endl;
        return "true true";
    } else {
        std::cout << "200 Ok\n" << std::endl;
        return "true false";
    }
}

std::string CommandProcessor::deleteFromBlacklist(const std::string& url) {
    if (url.empty()) {
        return "404 Not Found"; // Empty URL check
    }
    if (!(m_storageService->isInBlacklist(url))) {
        return "404 Not Found"; // URL not in blacklist
    }
    std::string standardURL = URLValidator().standardize(url);
    if (standardURL.empty() || !m_storageService->isInBlacklist(standardURL)) {
        return "404 Not Found"; // Invalid URL format or not in blacklist
    }
    
    if (m_storageService->removeFromBlacklist(standardURL)) {
        // Reload the blacklist to reflect changes
        std::unordered_set<std::string> updatedBlacklist;
        m_storageService->loadBlacklist(updatedBlacklist);
        m_bloomFilter->setBlackList(updatedBlacklist);
        return "204 No Content"; // Successfully deleted from blacklist
    }
    return "404 Not Found"; // URL not found in blacklist
}