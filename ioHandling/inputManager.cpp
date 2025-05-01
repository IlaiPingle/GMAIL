#include "inputManager.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../ioHandling/fileManager.h"
#include "../bloom_Filter/hashFactory.h"
#include <sstream>
#include <algorithm>

inputManager::inputManager(unique_ptr<bloomFilter> bloomFilter, unique_ptr<fileManager> fileManager)
    : m_bloomFilter(std::move(bloomFilter)), m_fileManager(std::move(fileManager)) {
        if ( this->m_fileManager->fileExistsAndNotEmpty("data/bloom.txt") && this->m_fileManager->fileExistsAndNotEmpty("data/bitArray.txt")) {
            this->m_fileManager->loadBloomFilter(*this->m_bloomFilter);
        }
    };

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
    m_fileManager->saveBloomFilter(*m_bloomFilter);
    return "";
}

string inputManager::runCheckBlacklist(const string& url) {
    if (!m_bloomFilter->contains(url)) {
        return "False";
    }
    else if (m_bloomFilter->containsAbsolutely(url)) {
        return "True true";

    }else {
        return "True false";
    }
}


unique_ptr <inputManager> inputManager::initfirstLine(const string& line) {
    stringstream iss(line);
    size_t bitArraySize;
    if (!(iss >> bitArraySize)) {
        return nullptr; // Invalid size
    }
    vector <size_t> hashInfos;
    size_t hashfuncinfo;
    while (iss >> hashfuncinfo) {
        hashInfos.push_back(hashfuncinfo);
    }
    if (hashInfos.empty()) {
        return nullptr; // Invalid hash function info
    }
    vector<shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
    unique_ptr<bloomFilter> bloomFilter = make_unique<bloomFilter>(bitArraySize, hashFunctions);
    unique_ptr<fileManager> fileManager = make_unique<fileManager>("data/bloom.txt", "data/bitArray.txt");

    return make_unique<inputManager>(move(bloomFilter), move(fileManager));
}
    

