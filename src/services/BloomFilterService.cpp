#include "BloomFilterService.h"


BloomFilterService::BloomFilterService(shared_ptr<IBloomFilter> filter, shared_ptr<IStorageService> storage)
: m_bloomFilter(move(filter)), m_storageService(move(storage)) {}

bool BloomFilterService::initialize(){
    vector<bool> bits = m_bloomFilter->getBitArray();
    bool bitArrayLoaded = m_storageService->loadBitArray(bits);
    m_bloomFilter->setBitArray(bits);
    unordered_set<string> blackList;
    bool blacklistLoaded = m_storageService->loadBlacklist(blackList);
    m_bloomFilter->setBlackList(blackList);
    return bitArrayLoaded || blacklistLoaded;
}
bool BloomFilterService::add(const string& url) {
    if (!m_bloomFilter->containsAbsolutely(url)) {
        m_bloomFilter->add(url);
        bool bitsSaved = m_storageService->saveBitArray(m_bloomFilter->getBitArray());
        bool blacklistSaved = m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
        return bitsSaved && blacklistSaved;
    }
    return false; // URL already exists in the filter
}

bool BloomFilterService::contains(const string &url) {
    return m_bloomFilter->contains(url);
}

bool BloomFilterService::containsAbsolutely(const string &url) {
    return m_bloomFilter->containsAbsolutely(url);
}
bool BloomFilterService::remove(const string &url) {
    if (!m_bloomFilter->containsAbsolutely(url)) {
        return true; // URL not found in the filter
    }
    return m_bloomFilter->remove(url) && m_storageService->removeFromBlacklist(url);
    
}

