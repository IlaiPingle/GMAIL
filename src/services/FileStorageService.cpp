#include "FileStorageService.h"
#include <fstream>
#include <iostream>
using namespace std;
bool FileStorageService::saveBlacklist(const unordered_set<string>& blacklist) {
    ofstream file(m_blacklistFile);
    if (!file) {
        return false;
    }
    for (const auto& url : blacklist) {
        file << url << endl;
    }
    return true;
}

bool FileStorageService::loadBlacklist(unordered_set<string>& blacklist) {
    ifstream file(m_blacklistFile);
    if (!file) {
        return false;
    }
    string url;
    while (getline(file, url)) {
        blacklist.insert(url);
    }
    return true;
}

bool FileStorageService::saveBitArray(const vector<bool>& bitArray) {
    ofstream file(m_bitArrayFile, ios::binary);
    if (!file) {
        return false;
    }
    size_t size = bitArray.size();
    file.write(reinterpret_cast<const char*>(&size), sizeof(size));
    // Pack bits into bytes for storage
    for (size_t i = 0; i < size; i += 8) {
        unsigned char byte = 0;
        for (size_t j = 0; j < 8 && (i + j) < size; ++j) {
            if (bitArray[i + j]) {
                byte |= (1 << j);
            }
        }
        file.write(reinterpret_cast<const char*>(&byte), sizeof(byte));
    }
    return true;
}

bool FileStorageService::loadBitArray(vector<bool>& bitArray) {
    ifstream file(m_bitArrayFile, ios::binary);
    if (!file) {
        return false;
    }
    size_t size;
    file.read(reinterpret_cast<char*>(&size), sizeof(size));
    bitArray.resize(size);
    // Unpack bytes into bits
    for (size_t i = 0; i < size; i += 8) {
        unsigned char byte;
        file.read(reinterpret_cast<char*>(&byte), sizeof(byte));
        
        for (size_t j = 0; j < 8 && (i + j) < size; ++j) {
            bitArray[i + j] = (byte & (1 << j)) != 0;
        }
    }
    return true;
}

bool FileStorageService::removeFromBlacklist(const string& url) {
    // Read all URLs except the one to delete
    vector<string> urls;
    ifstream inFile(m_blacklistFile);
    string line;
    if (!inFile) {
        return false;
    }
    while (getline(inFile, line)) {
        if (line != url) {
            urls.push_back(line);
        }
    }
    inFile.close();
    // Write back all URLs except the deleted one
    ofstream outFile(m_blacklistFile);
    if (!outFile) {
        return false;
    }
    for (const string& u : urls) {
        outFile << u << endl;
    }
    return true;
}

bool FileStorageService::isInBlacklist(const string& url) {
    ifstream file(m_blacklistFile);
    string line;
    if (!file) {
        return false;
    }
    while (getline(file, line)) {
        if (line == url) {
            return true;
        }
    }
    return false;
}

bool FileStorageService::fileExistsAndNotEmpty(const string& filename) {
    ifstream file(filename);
    return file.good() && file.peek() != ifstream::traits_type::eof();
}

bool FileStorageService::initializeFilter(vector<bool>& bitArray, unordered_set<string>& blacklist) {
    bool bitArrayLoaded = false;
    if (fileExistsAndNotEmpty(m_bitArrayFile)) {
        bitArrayLoaded = loadBitArray(bitArray);
    }

    bool blacklistLoaded = false;
    if (fileExistsAndNotEmpty(m_blacklistFile)) {
        blacklistLoaded = loadBlacklist(blacklist);
    }

    return bitArrayLoaded || blacklistLoaded;
}