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
        fileManager();

        fileManager(const string& filterFilePath, const string& bitArrayFilePath);

        fileManager(const fileManager&) = default; // Copy constructor

        bool saveBloomFilter(const bloomFilter& filter) const;

        bool loadBloomFilter(bloomFilter& filter) const;

        bool saveBitArray(const vector<bool>& bitArray) const;

        bool loadBitArray(vector<bool>& bitArray, const string& filepath) const;

        bool fileExistsAndNotEmpty(const string& filename) const;

        ~fileManager();
};
#endif // FILE_MANAGER_H