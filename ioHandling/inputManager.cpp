#include "inputManager.h"
#include <sstream>
#include <algorithm>

inputManager::inputManager(unique_ptr<BloomFilter> bloomFilter, unique_ptr<FileManager> fileManager)
    : bloomFilter(std::move(bloomFilter)), fileManager(std::move(fileManager)) {
        if ( this->fileManager->fileExists()){
            this->fileManager->loadBloomFilter(*this->bloomFilter);
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
    bloomFilter->add(url);
    fileManager->saveBloomFilter(*bloomFilter);
    return "";
}

string inputManager::runCheckBlacklist(const string& url) {
    if (!bloomFilter->contains(url)) {
        return "False";
    }
    else if (bloomFilter->containsAbsolutely(url)) {
        return "True" + " true";

    }else {
        return "True" + " false";
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
    while (iss >> hashfunc) {
        hashInfos.push_back(hashfunc);
    }
    if (hashInfos.empty()) {
        return nullptr; // Invalid hash function info
    }
    vector <shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
    unique_ptr<BloomFilter> bloomFilter = make_unique<BloomFilter>(bitArraySize, hashFunctions);
    unique_ptr<FileManager> fileManager = make_unique<FileManager>("data/bloom.txt", "data/bitArray.txt");

    return make_unique<inputManager>(move(bloomFilter), move(fileManager));
}
    

