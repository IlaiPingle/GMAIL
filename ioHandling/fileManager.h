#ifndef FILE_MANAGER_H
#define FILE_MANAGER_H

#include <string>
#include <vector>
#include "../bloom_Filter/bloomFilter.h" 
using namespace std;

class fileManager {
    private:
        string m_filterFilePath;
        string m_bitArrayFilePath;

    public:
        fileManager(const string& filterFilePath, const string& bitArrayFilePath);

        bool saveBloomFilter(const bloomFilter& filter) const;

        bool loadBloomFilter(bloomFilter& filter, string m_filterFilePath) const;

        bool saveBitArray(const vector<bool>& bitArray) const;

        bool loadBitArray(vector<bool>& bitArray, string m_bitArrayFilePath) const;

        bool fileExistsAndNotEmpty(const string& filename) const;

        ~fileManager();
};
#endif // FILE_MANAGER_H