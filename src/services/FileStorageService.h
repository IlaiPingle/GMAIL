#ifndef FILESTORAGESERVICE_H
#define FILESTORAGESERVICE_H


#include "../bloom_Filter/bloomFilter.h"
#include <string>
#include <unordered_set>
#include <vector>
#include <memory>
#include <fstream>
#include <iostream>
using namespace std;
/**
* @class FileStorageService
* @brief A class that implements the IStorageService interface for file-based storage.
*
* This class provides methods to save and load bit arrays and blacklists from files.
*/
class FileStorageService {
    private:
    string m_blacklistFile;
    bool saveBlacklist(const unordered_set<string>& blacklist);
    
    public:
    FileStorageService(const string& blacklistPath = "data/blacklist.txt")
    : m_blacklistFile(blacklistPath){}
    bool addToBlacklist(const string& url) ;
    bool loadBlacklist(unordered_set<string>& blacklist) ;
    bool loadBitArray(const shared_ptr<bloomFilter>& bloomFilter);
    bool removeFromBlacklist(const string& url) ;
    bool isInBlacklist(const string& url) ; 
};
#endif // FILESTORAGESERVICE_H