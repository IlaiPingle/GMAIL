#include <vector>
#include <string>
#include "bloomFilter.h"
#include <functional> // For std::hash
#include <fstream> // For std::ifstream
#include <stdexcept> // For std::runtime_error
#include "repeatedHash.h"

using namespace std;

// Hash function implementation
/*int bloomFilter::hash(const string& key, int seed) const {
    // Simple hash function implementation using std::hash with a seed
    std::hash<string> hasher;
    unsigned int hashValue = hasher(key);
    return (hashValue ^ seed) % bitArray.size();
}*/
// Helper function to check if a file exists
bool bloomFilter::fileExists(const string& filename) const {
    ifstream file(filename);
    return file.good();
}
// Constructor implementation
bloomFilter::bloomFilter(size_t size, const std::vector<std::shared_ptr<hashable>>& hashFuncs)
    : m_bitArray(size, false), m_hashFunctions(hashFuncs), m_arraySize(size) {};
// Constructor with size and number of hash functions
bloomFilter::bloomFilter(int size, int numHashes)
    : m_bitArray(size, false), m_arraySize(size), numHashFunctions(numHashes) {
    // Initialize hash functions
    for (int i = 0; i < numHashes; ++i) {
        m_hashFunctions.push_back(std::make_shared<repeatedHash>(i + 1, size));
    }
}


// Add implementation
void bloomFilter::add(const std::string& url) {
    for (const auto& func : m_hashFunctions) {
        size_t index = (*func)(url) % m_arraySize;
        m_bitArray[index] = true;
    }
    /* NEED TO SAVE REAL URL IN THE BLACKLIST FILE HERE!!!*/
}

bool bloomFilter::contains(const std::string& url) const {
    for (const auto& func :m_hashFunctions) {
        size_t index = (*func)(url) % m_arraySize;
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



/*bool checkFalsePositive(const bloomFilter& bf, const std::string& url) {
    if(bf.contains(url)) {
        // check if url is in the real blcklisst file:
        while 
        
        return true; // False positive
    }
}*/

