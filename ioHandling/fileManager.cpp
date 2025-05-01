#include "fileManager.h"
#include <fstream>
#include <iostream>
#include <filesystem>

using namespace std;

fileManager::fileManager(const string& blackListFilePath, const string& bitArrayFilePath)
    : m_blackListFilePath(blackListFilePath), m_bitArrayFilePath(bitArrayFilePath) {}
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

bool fileManager::loadBitArray(vector<bool>& bitArray) const {
    if (!fileExistsAndNotEmpty(m_bitArrayFilePath)) {
        throw runtime_error("File does not exist.");
    }
    ifstream inFile(m_bitArrayFilePath, ios::binary);
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
        bitArray[i] = (byte != 0);      // Convert byte to bool
    }
    inFile.close();
    return true;
}
bool fileManager::saveBlackList(const unordered_set<string>& blackList) const {
    ofstream outFile(m_blackListFilePath);
    if (!outFile) {
        throw runtime_error("Failed to open blacklist file for writing.");
    }

    for (const auto& url : blackList) {
        outFile << url << '\n';
    }

    return true;
}
bool fileManager::loadBlackList(unordered_set<string>& blackList) const {
    if (!fileExistsAndNotEmpty(m_blackListFilePath)) {
        return false;
    }
    ifstream inFile(m_blackListFilePath);
    if (!inFile) {
        throw runtime_error("Failed to open blacklist file for reading.");
    }
    string url;
    while (getline(inFile, url)) {
        if (!url.empty()) {
            blackList.insert(url);
        }
    }
    return true;
}


bool fileManager::fileExistsAndNotEmpty(const string& filename) const {
    return filesystem::exists(filename) && filesystem::file_size(filename) > 0;
}

fileManager::~fileManager() {}



    

