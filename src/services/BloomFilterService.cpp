#include "BloomFilterService.h"
using namespace std;

BloomFilterService::BloomFilterService(std::shared_ptr<IBloomFilter> filter, std::shared_ptr<IStorageService> storage)
    : m_bloomFilter(std::move(filter)), m_storageService(std::move(storage)) {}
    
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
    m_bloomFilter->add(url);
    bool bitsSaved = m_storageService->saveBitArray(m_bloomFilter->getBitArray());
    bool blacklistSaved = m_storageService->saveBlacklist(m_bloomFilter->getBlackList());
    return bitsSaved && blacklistSaved;
}

bool BloomFilterService::contains(const std::string &url) {
    return m_bloomFilter->contains(url);
}

bool BloomFilterService::containsAbsolutely(const std::string &url) {
    return m_bloomFilter->containsAbsolutely(url);
}

