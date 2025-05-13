#ifndef FILESTORAGESERVICE_H
#define FILESTORAGESERVICE_H

#include "../interfaces/IStorageService.h"
#include <string>
#include <unordered_set>
#include <vector>
using namespace std;
class FileStorageService : public IStorageService {
private:
    string m_bitArrayFile;
    string m_blacklistFile;
    string m_configFile;

public:
    FileStorageService(const string& bitArrayPath = "data/bit_array.dat",
                       const string& blacklistPath = "data/blacklist.txt",
                       const string& configPath = "data/config.txt")
        : m_bitArrayFile(bitArrayPath), m_blacklistFile(blacklistPath) ,m_configFile(configPath){}
    bool saveBlacklist(const unordered_set<string>& blacklist) override;
    bool loadBlacklist(unordered_set<string>& blacklist) override;
    bool saveBitArray(const vector<bool>& bitArray) override;
    bool loadBitArray(vector<bool>& bitArray) override;
    bool configFromFile(string& configLine) override;
    bool removeFromBlacklist(const string& url) override;
    bool isInBlacklist(const string& url) override;
    bool fileExistsAndNotEmpty(const string& filename) override;
    bool initializeFilter(vector<bool>& bitArray, unordered_set<string>& blacklist);

};
#endif // FILESTORAGESERVICE_H