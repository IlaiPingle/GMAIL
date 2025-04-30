#include "fileManager.h"
#include <fstream>
#include <iostream>
using namespace std;

bool fileManager :: saveBloomFilter(const bloomFilter& filter) const {
    ofstream outFile(filename, ios::binary);
    if (!outFile) {
       throw runtime_error("Failed to open file for saving Bloom filter.");
    }
    for (bool bit : m_bitArray) {
        char byte = bit ? 1 : 0; // Convert bool to byte (1 or 0)
        outFile.write(&byte, sizeof(byte));
    }
    outFile.close();
    return true;
}


bool fileManager :: loadBloomFilter(const bloomFilter& filter) const {
    if (!fileExists(filename)) {
        throw runtime_error("File does not exist.");
    }

    ifstream inFile(filename, ios::binary);
    if (!inFile) {
        throw runtime_error("Failed to open file for loading Bloom filter.");
    }

    // Validate file size
    inFile.seekg(0, ios::end);
    size_t fileSize = inFile.tellg();
    inFile.seekg(0, ios::beg);

    if (fileSize != m_bitArray.size() * sizeof(bool)) {
        throw runtime_error("File size does not match Bloom filter size.");
    }

    for (size_t i = 0; i < m_bitArray.size(); ++i) {
        char byte;
        inFile.read(&byte, sizeof(byte));
        filter -> m_bitArray[i] = (byte != 0); // Convert byte to bool
    }
    inFile.close();
    return true;
}



    

