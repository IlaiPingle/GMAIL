#include "BloomFilterService.h"


BloomFilterService::BloomFilterService(shared_ptr<bloomFilter> filter, shared_ptr<FileStorageService> storage)
: m_bloomFilter(move(filter)), m_storageService(move(storage)) {}

bool BloomFilterService::initialize() {
    bool isBitArrayLoaded = m_storageService->loadBitArray(m_bloomFilter);
    m_bloomFilter->setBitArray(m_bloomFilter->getBitArray());
    unordered_set<string> blackList;
    bool isBlacklistLoaded = m_storageService->loadBlacklist(blackList);
    m_bloomFilter->setBlackList(blackList);
    return isBitArrayLoaded && isBlacklistLoaded;
}

bool BloomFilterService::add(const string& url) {
    if (!contains(url)) {
        m_bloomFilter->add(url);
        bool isBlacklistSaved = m_storageService->addToBlacklist(url);
        return isBlacklistSaved;
    } else if (!m_bloomFilter->containsAbsolutely(url)) {
        m_bloomFilter->add(url);
        bool isBlacklistSaved = m_storageService->addToBlacklist(url);
        return isBlacklistSaved; // if false thers an error in saving to blacklist
    }
    return true; // URL already blacklisted , command is legal but no action needed
}

bool BloomFilterService::remove(const string &url) {
    if (!m_bloomFilter->containsAbsolutely(url)) {
        return false; // URL not found in the filter
    }
    return m_bloomFilter->remove(url) && m_storageService->removeFromBlacklist(url);
}

bool BloomFilterService::contains(const string& url) {
    return m_bloomFilter->contains(url);
}
bool BloomFilterService::containsAbsolutely(const string& url) {
    return m_bloomFilter->containsAbsolutely(url);
}