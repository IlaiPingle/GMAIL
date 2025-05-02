#include "inputManager.h"
#include "../bloom_Filter/bloomFilter.h"
#include "../ioHandling/fileManager.h"
#include "../bloom_Filter/hashFactory.h"
#include <sstream>
#include <algorithm>
#include <string>

inputManager::inputManager() : m_bloomFilter(nullptr), m_fileManager(nullptr) {}

inputManager::inputManager(unique_ptr<bloomFilter> bloomFilter,unique_ptr<fileManager> fileManager)
    : m_bloomFilter(move(bloomFilter)), m_fileManager(move(fileManager)) {
        tryLoadFile();
    }
inputManager::~inputManager() {} 


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
            return "Error: Invalid command";
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
    
    if (standardUrl.find("https://") == 0) {
        standardUrl = standardUrl.substr(8);  // Remove "https://"
    } else if (standardUrl.find("http://") == 0) {
        standardUrl = standardUrl.substr(7);  // Remove "http://"
    }
    if (standardUrl.find("www.") != 0) {
        return ""; // Invalid URL format (not starting with "www.")
    }
    
    return standardUrl;
}

string inputManager::runAddToBlacklist(const string& url) {
    string standardURL = standardizeURL(url);
    if (standardURL.empty()) {
        return ""; // Invalid URL format
    }
    m_bloomFilter->add(standardURL);
    m_fileManager->saveBitArray(m_bloomFilter->getBitArray());
    m_fileManager->saveBlackList(m_bloomFilter->getBlackList());
    return "";
}

string inputManager::runCheckBlacklist(const string& url) {
    if (url.empty()) {
        return "false"; // Empty URL check
    }
    string standardURL = standardizeURL(url);
    if (standardURL.empty()) {
        return "false"; // Invalid URL format
    }
    if (!m_bloomFilter->contains(standardURL)) {
        return "false";
    }
    else if (m_bloomFilter->containsAbsolutely(standardURL)) {
        return "true true";
    }else {
        return "true false";
    }
}


unique_ptr <inputManager> inputManager::initFirstLine(const string& line) {
    istringstream iss(line);
    size_t bitArraySize;
    vector <size_t> hashInfos;
    if (!(iss >> bitArraySize)) {
        return nullptr; // Invalid size
    }
    size_t hashId;
    while (iss >> hashId) {
        hashInfos.push_back(hashId);
    }
    if (hashInfos.empty()) {
        return nullptr; // Invalid hash function info
    }
    try{
        auto hashFunctions = hashFactory::createHashFunctions(hashInfos);
        auto filter = make_unique<bloomFilter>(bitArraySize, hashFunctions);
        auto fileMgr = make_unique<fileManager>("data/blacklist.txt", "data/bit_array.dat");
        return make_unique<inputManager>(move(filter), move(fileMgr));
    }
    catch (const std::exception& e) {
        return nullptr; // Initialization failed
    }

    
}
    

