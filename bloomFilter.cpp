#include <vector>
#include <string>
#include "bloomFilter.h"
#include <functional> // For std::hash
#include <fstream> // For std::ifstream
#include <stdexcept> // For std::runtime_error
#include "repeatedHash.h"

using namespace std;

// Helper function to check if a file exists
bool bloomFilter::fileExists(const string& filename) const {
    ifstream file(filename);
    return file.good();
}
// Constructor implementation
bloomFilter::bloomFilter(size_t size, const vector<function<size_t(const string&)>>& hashFuncs)
    : m_bitArray(size, false), m_hashFunctions(hashFuncs), m_arraySize(size) {}
// Constructor with size and number of hash functions
bloomFilter::bloomFilter(int size, int numHashes)
    : m_bitArray(size, false), m_arraySize(size), numHashFunctions(numHashes) {
    // Initialize hash functions
    for (int i = 0; i < numHashes; ++i) {
        m_hashFunctions.push_back([hash = repeatedHash(i + 1, size)](const string& input) {
            return hash(input);
        });
    }
}


// Add implementation
void bloomFilter::add(const string& url) {
    for (const auto& func : m_hashFunctions) {
        size_t index = func(url) % m_arraySize;
        m_bitArray[index] = true;
    }
    m_blackList.insert(url); // Store the real URL in the blacklist
}

bool bloomFilter::contains(const string& url) const {
    for (const auto& func :m_hashFunctions) {
        size_t index = func(url) % m_arraySize;
        if (!m_bitArray[index])
            return false;
    }
    return true;
}
//save the bloom filter to a file
void bloomFilter::saveToFile(const string& filename) const {
    ofstream outFile(filename, ios::binary);
    if (!outFile) {
       throw runtime_error("Failed to open file for saving Bloom filter.");
    }
    for (bool bit :m_bitArray) {
        char byte = bit ? 1 : 0; // Convert bool to byte (1 or 0)
        outFile.write(&byte, sizeof(byte));
    }
    outFile.close();
}
//load the bloom filter from a file
void bloomFilter::loadFromFile(const string& filename) {
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
        m_bitArray[i] = (byte != 0); // Convert byte to bool
    }
    inFile.close();
}

bool bloomFilter::isFalsePositive(const string& url) const {
    return contains(url) && m_blackList.find(url) == m_blackList.end();
}
bool bloomFilter::checkFalsePositive(const bloomFilter& bf, const string& url) {
    return bf.isFalsePositive(url);
}

