#ifndef FILE_MANAGER_H
#define FILE_MANAGER_H

#include <string>
#include <vector>
#include "../bloom_Filter/bloomFilter.h" 
using namespace std;

class fileManager {
    private:
        string m_blackListFilePath;
        string m_bitArrayFilePath;

    public:
        // Constructor
        fileManager(const string& blackListFilePath, const string& bitArrayFilePath);

        // save and load
        bool saveBloomFilter(const bloomFilter& filter) const;
        bool loadBloomFilter(bloomFilter& filter) const;
        
        bool saveBlackList(const unordered_set<string>& blackList) const;
        bool loadBlackList(unordered_set<string>& blackList) const;

        // save and load bit array
        bool saveBitArray(const vector<bool>& bitArray) const;
        bool loadBitArray(vector<bool>& bitArray) const;

        // file utility function
        bool fileExistsAndNotEmpty(const string& filename) const;

        ~fileManager();
};
#endif // FILE_MANAGER_H