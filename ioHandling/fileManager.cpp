#include "fileManager.h"
#include <fstream>
#include <iostream>
#include <filesystem>

using namespace std;

// Default constructor
fileManager::fileManager() : m_filterFilePath("data/bloom.txt"), m_bitArrayFilePath("data/bitArray.txt") {}

fileManager::fileManager(const string& filterFilePath, const string& bitArrayFilePath)
    : m_filterFilePath(filterFilePath), m_bitArrayFilePath(bitArrayFilePath) {}

bool fileManager :: saveBloomFilter(const bloomFilter& filter) const {
    const unordered_set<string>& blackList = filter.getBlackList();
    vector<bool> bitArray(blackList.size(), false); // Adjust size and initialize
    // Conversion logic if needed
    ofstream outFile(m_filterFilePath, ios::binary);
    if (!outFile) {
       throw runtime_error("Failed to open file for saving Bloom filter.");
    }
    size_t filterSize = filter.getBlackList().size();
    for (bool bit : bitArray) {
        char byte = bit ? 1 : 0; // Convert bool to byte (1 or 0)
        outFile.write(&byte, sizeof(byte));
    }
    outFile.close();
    return true;
}


bool fileManager :: loadBloomFilter(bloomFilter& filter) const {
    if (!fileExistsAndNotEmpty(m_filterFilePath) || !fileExistsAndNotEmpty(m_bitArrayFilePath)) {
        throw runtime_error("File does not exist or is empty.");
    }
    const unordered_set<string>& blackList = filter.getBlackList();
    vector<bool> bitArray(blackList.size(), false); // Adjust size and initialize
    ifstream inFile(m_bitArrayFilePath, ios::binary);
    if (!inFile) {
        throw runtime_error("Failed to open file for loading Bloom filter.");
    }

    // Validate file size
    inFile.seekg(0, ios::end);
    size_t fileSize = inFile.tellg();
    inFile.seekg(0, ios::beg);

    size_t expectedSize = bitArray.size(); // Assuming bitArray.size() is the expected size
    if (fileSize != expectedSize) {
        throw runtime_error("File size does not match Bloom filter size.");
    }
    for (size_t i = 0; i < expectedSize; ++i) {
        char byte;
        inFile.read(&byte, sizeof(byte));
        bitArray[i] = (byte != 0); // Convert byte to bool
    }
    inFile.close();
    return true;
}

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

bool fileManager::loadBitArray(vector<bool>& bitArray, const string& filepath) const {
    string pathToUse = filepath.empty() ? m_bitArrayFilePath : filepath;
    if (!fileExistsAndNotEmpty(pathToUse)) {
        throw runtime_error("File does not exist.");
    }
    ifstream inFile(pathToUse, ios::binary);
    if (!inFile) {
        throw runtime_error("Failed to open file for loading bit array.");
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

fileManager::~fileManager() {}



    

