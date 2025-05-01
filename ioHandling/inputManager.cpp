#include "inputManager.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../ioHandling/fileManager.h"
#include "../bloom_Filter/hashFactory.h"
#include <sstream>
#include <algorithm>
#include <iostream>
#include <string>

inputManager::inputManager(unique_ptr<bloomFilter> bloomFilter,unique_ptr<fileManager> fileManager)
    : m_bloomFilter(move(bloomFilter)), m_fileManager(move(fileManager)) {
        tryLoadFile();
    }



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

    string inputManager::convertLine(const string& line) {
        istringstream iss(line);
        int command;
    
        if (!(iss >> command)) {
            return ""; // Invalid command format
        }
        
        if (command != 1 && command != 2) {
            return "Error: Invalid command"; // Invalid command
        }
        
        // Check if there's anything after the command number
        string remaining = line.substr(line.find_first_of("12") + 1);
        
        // If there's nothing after the command number (not even a space)
        if (remaining.empty()) {
            return "Error: Missing URL";
        }
        
        // Check if there's at least a space after the command
        if (remaining[0] != ' ') {
            return "Error: Missing URL";
        }
        
        // Extract URL and trim leading whitespace
        string url;
        getline(iss >> ws, url);
        
        // For command 1, allow empty URL (the test expects this)
        if (command == 1) {
            return runAddToBlacklist(url);
        } else { // command == 2
            return runCheckBlacklist(url);
        }
    }

string inputManager::standardizeURL(const string& url) {
    string standardUrl = url;
    size_t start = standardUrl.find_first_not_of(" \t\n\r");
    if (start != string::npos) {
        standardUrl = standardUrl.substr(start); // Remove leading whitespace
    } else {
        return ""; // Empty URL after trimming
    }
    size_t end = standardUrl.find_last_not_of(" \t\n\r");
    if (end != string::npos) {
        standardUrl = standardUrl.substr(0, end + 1); // Remove trailing whitespace
    }
    // Convert to lowercase
    transform(standardUrl.begin(), standardUrl.end(), standardUrl.begin(),
     [](unsigned char c) {return tolower(c);});
    
    return standardUrl;
}

string inputManager::runAddToBlacklist(const string& url) {
    m_bloomFilter->add(url);
    m_fileManager->saveBitArray(m_bloomFilter->getBitArray());
    m_fileManager->saveBlackList(m_bloomFilter->getBlackList());
    return "";
}

string inputManager::runCheckBlacklist(const string& url) {
    if (url.empty()) {
        return "False"; // Empty URL check
    }
    string standardURL = standardizeURL(url);
    if (!m_bloomFilter->contains(standardURL)) {
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
        cerr << "Invalid bit array size" << endl;
        return nullptr; // Invalid size
    }
    vector <size_t> hashInfos;
    size_t hashId;
    while (iss >> hashId) {
        hashInfos.push_back(hashId);
    }
    if (hashInfos.empty()) {
        cerr << "Invalid hash function parameters provided" << endl;
        return nullptr; // Invalid hash function info
    }
    vector<shared_ptr<hashable>> hashFunctions = hashFactory::createHashFunctions(hashInfos);
    unique_ptr<bloomFilter> filter = make_unique<bloomFilter>(bitArraySize, hashFunctions);
    unique_ptr<fileManager> manager = make_unique<fileManager>("data/blackList.txt", "data/bitArray.txt");

    return make_unique<inputManager>(move(filter), move(manager));
}.
    

