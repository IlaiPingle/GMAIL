#include "CommandProcessor.h"
#include "../utils/URLValidator.h"

CommandProcessor::CommandProcessor(std::unique_ptr<IBloomFilter> bloomFilter,
                                 std::unique_ptr<IStorageService> storageService)
    : m_bloomFilter(std::move(bloomFilter)), m_storageService(std::move(storageService)) {}

std::string CommandProcessor::addToBlacklist(const std::string& url) {
    URLValidator urlValidator;
    std::string standardURL = urlValidator.standardize(url);
    if (standardURL.empty()) {
        return ""; // Invalid URL format
    }
    m_bloomFilter->add(standardURL);
    m_storageService->saveBitArray(m_bloomFilter->getBitArray());
    m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
    return "";
}

std::string CommandProcessor::checkBlacklist(const std::string& url) {
    URLValidator urlValidator;
    std::string standardURL = urlValidator.standardize(url);
    if (url.empty()) {
        return "false"; // Empty URL check
    }
    if (standardURL.empty()) {
        return "false"; // Invalid URL format
    }
    if (!m_bloomFilter->contains(standardURL)) {
        return "false";
    }
    else if (m_bloomFilter->containsAbsolutely(standardURL)) {
        return "true true";
    } else {
        return "true false";
    }
}

/*std::string CommandProcessor::deleteFromBlacklist(const std::string& url) {
    if (url.empty()) {
        return "false"; // Empty URL check
    }
    std::string standardURL = URLService::standardizeURL(url);
    if (standardURL.empty()) {
        return "false"; // Invalid URL format
    }
    
    if (m_storageService->removeFromBlacklist(standardURL)) {
        // Reload the blacklist to reflect changes
        std::unordered_set<std::string> updatedBlacklist;
        m_storageService->loadBlacklist(updatedBlacklist);
        m_bloomFilter->setBlackList(updatedBlacklist);
        return "true";
    }
    return "false";
}*/