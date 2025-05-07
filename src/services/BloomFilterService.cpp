#include "BloomFilterService.h"
using namespace std;

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

/*bool BloomFilterService::remove(const std::string &url) {
    if (!m_storageService->isInBlacklist(url)) {
        return false;
    }
    bool success = m_storageService->removeFromBlacklist(url);
    if (success) {
        // Reload blacklist to reflect changes
        std::unordered_set<std::string> bl;
        m_storageService->loadBlacklist(bl);
        m_bloomFilter->setBlackList(bl);
    }
    return success;
}*/
