#include "BloomFilterService.h"


BloomFilterService::BloomFilterService(shared_ptr<IBloomFilter> filter, shared_ptr<IStorageService> storage)
: m_bloomFilter(move(filter)), m_storageService(move(storage)) {}

bool BloomFilterService::initialize() {
    vector<bool> bitArray = m_bloomFilter->getBitArray();
    bool isBitArrayLoaded = m_storageService->loadBitArray(bitArray);
    m_bloomFilter->setBitArray(bitArray);
    unordered_set<string> blackList;
    bool isBlacklistLoaded = m_storageService->loadBlacklist(blackList);
    m_bloomFilter->setBlackList(blackList);
    return isBitArrayLoaded && isBlacklistLoaded;
}

bool BloomFilterService::add(const string& url) {
    if (!m_bloomFilter->containsAbsolutely(url)) {
        m_bloomFilter->add(url);
        bool isBitsSaved = m_storageService->saveBitArray(m_bloomFilter->getBitArray());
        bool isBlacklistSaved = m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
        return isBitsSaved && isBlacklistSaved;
    }
    return false; // URL already exists in the filter
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