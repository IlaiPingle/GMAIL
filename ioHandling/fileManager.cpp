#include "fileManager.h"
#include <fstream>
#include <iostream>
#include <filesystem>
using namespace std;

fileManager::fileManager(const string& filterFilePath, const string& bitArrayFilePath)
    : m_filterFilePath(filterFilePath), m_bitArrayFilePath(bitArrayFilePath) {}

bool fileManager :: saveBloomFilter(const bloomFilter& filter) const {
    ofstream outFile(m_filterFilePath, ios::binary);
    if (!outFile) {
       throw runtime_error("Failed to open file for saving Bloom filter.");
    }
    size_t filterSize = filter.getBlackList().size();
    for (bool bit : m_bitArray) {
        char byte = bit ? 1 : 0; // Convert bool to byte (1 or 0)
        outFile.write(&byte, sizeof(byte));
    }
    outFile.close();
    return true;
}


bool fileManager :: loadBloomFilter(bloomFilter& filter, string m_filterFilePath) const {
    if (!fileExistsAndNotEmpty(m_filterFilePath)) {
        throw runtime_error("File does not exist.");
    }

    ifstream inFile(m_bitArrayFilePath, ios::binary);
    if (!inFile) {
        throw runtime_error("Failed to open file for loading Bloom filter.");
    }

    // Validate file size
    inFile.seekg(0, ios::end);
    size_t fileSize = inFile.tellg();
    inFile.seekg(0, ios::beg);

    size_t expectedSize = filter.getBlackList().size();
    if (fileSize != expectedSize) {
        throw runtime_error("File size does not match Bloom filter size.");
    }
    for (size_t i = 0; i < filter.getBlackList().size(); ++i) {
        char byte;
        inFile.read(&byte, sizeof(byte));
        filter.getBlackList()[i] = (byte != 0); // Convert byte to bool
    }
    inFile.close();
    return true;
}

fileManager::~fileManager() {}

bool fileManager::saveBitArray(const vector<bool>& bitArray) const {
    ofstream outFile(m_bitArrayFilePath, ios::binary);
    if (!outFile) {
        throw runtime_error("Failed to open file for saving bit array.");
    }
    for (bool bit : bitArray) {
        char byte = bit ? 1 : 0; // Convert bool to byte (1 or 0)
        outFile.write(&byte, sizeof(byte));
    }
    outFile.close();
    return true;
}

bool fileManager::loadBitArray(vector<bool>& bitArray, string m_bitArrayFilePath) const {
    if (!fileExistsAndNotEmpty(m_bitArrayFilePath)) {
        throw runtime_error("File does not exist.");
    }

    // Validate file size
    inFile.seekg(0, ios::end);
    size_t fileSize = inFile.tellg();
    inFile.seekg(0, ios::beg);

    if (fileSize != bitArray.size()) {
         // Assuming bitArray.size() is the expected size
        throw runtime_error("File size does not match bit array size.");
    }

    for (size_t i = 0; i < bitArray.size(); ++i) {
        char byte;
        inFile.read(&byte, sizeof(byte));
        bitArray[i] = (byte != 0); // Convert byte to bool
    }
    inFile.close();
    return true;
}
bool fileManager::fileExistsAndNotEmpty(const string& filename) const {
    return filesystem::exists(filename) && filesystem::file_size(filename) > 0;
}



    

