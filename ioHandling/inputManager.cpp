#include "inputManager.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../ioHandling/fileManager.h"
#include "../bloom_Filter/hashFactory.h"
#include <sstream>
#include <algorithm>

inputManager::inputManager(unique_ptr<bloomFilter> bloomFilter, unique_ptr<fileManager> fileManager)
    : m_bloomFilter(move(bloomFilter)), m_fileManager(move(fileManager)) {
        if ( this->m_fileManager->fileExistsAndNotEmpty("data/blackList.txt") && this->m_fileManager->fileExistsAndNotEmpty("data/bitArray.txt")) {
            this->m_fileManager->loadBloomFilter(*this->m_bloomFilter);
        }
    };

void inputManager::tryLoadFile() {
    // טוען את ה-bit array אם הקובץ קיים
    vector<bool> bits = m_bloomFilter->getBitArray();
    if (m_fileManager->fileExistsAndNotEmpty("data/bit_array.dat")) {
        m_fileManager->loadBitArray(bits);
        m_bloomFilter->setBitArray(bits);
    }

    // טוען את ה-blacklist אם הקובץ קיים
    unordered_set<string> bl;
    if (m_fileManager->fileExistsAndNotEmpty("data/blacklist.txt")) {
        m_fileManager->loadBlackList(bl);
        m_bloomFilter->setBlackList(bl);
    }
}    

string inputManager:: convertLine(const string& line) {
    istringstream iss(line);
    int command;

    if (!(iss >> command)) {
        return ""; // Invalid command
    }
    string url;
    getline(iss >> ws , url);
    switch (command) {
        case 1:
            return runAddToBlacklist(url);
        case 2:
            return runCheckBlacklist(url);
        default:
            // Invalid command
            return "";
    }
}

string inputManager::runAddToBlacklist(const string& url) {
    m_bloomFilter->add(url);
    m_fileManager->saveBitArray(m_bloomFilter->getBitArray());
    m_fileManager->saveBlackList(m_bloomFilter->getBlackList());
    return "";
}

string inputManager::runCheckBlacklist(const string& url) {
    if (!m_bloomFilter->contains(url)) {
        return "False";
    }
    else if (m_bloomFilter->containsAbsolutely(url)) {
        return "True True";
    }else {
        return "True False";
    }
}


unique_ptr <inputManager> inputManager::initFirstLine(const string& line) {
    stringstream iss(line);
    size_t bitArraySize;
    if (!(iss >> bitArraySize)) {
        return nullptr; // Invalid size
    }
    vector <size_t> hashInfos;
    size_t hashId;
    while (iss >> hashId) {
        hashInfos.push_back(hashId);
    }
    if (hashInfos.empty()) {
        return nullptr; // Invalid hash function info
    }
    vector<shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
    unique_ptr<bloomFilter> filter = make_unique<bloomFilter>(bitArraySize, hashFunctions);
    unique_ptr<fileManager> manager = make_unique<fileManager>("data/blackList.txt", "data/bitArray.txt");

    return make_unique<inputManager>(move(filter), move(manager));
}
    

