#include <vector>
#include <string>
#include "bloomFilter.h"
#include <functional> // For std::hash
#include <fstream> // For std::ifstream
#include <stdexcept> // For std::runtime_error

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


// Add implementation
void bloomFilter::add(const std::string& url) {
    for (const auto& func : m_hashFunctions) {
        size_t index = (*func)(url);
        m_bitArray[index] = true;
    }
    /* NEED TO SAVE REAL URL IN THE BLACKLIST FILE HERE!!!*/
}

bool bloomFilter::contains(const std::string& url) const {
    for (const auto& func :m_hashFunctions) {
        size_t index = (*func)(url);
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
    for (bool bit : bitArray) {
        outFile.write(reinterpret_cast<const char*>(&bit), sizeof(bit));
    }
    outFile.close();
}
//load the bloom filter from a file
void bloomFilter::loadFromFile(const string& filename) {
    ifstream inFile(filename, ios::binary);
    if (!inFile) {
        throw runtime_error("Failed to open file for loading Bloom filter.");
    }
    if (!fileExists(filename)) {
        throw runtime_error("File does not exist.");
        saveToFile(filename); // Save the current state to a file
    }
    
    for (bool bit : bitArray) {
            inFile.read(reinterpret_cast<char*>(&bit), sizeof(bit));
        }
        inFile.close();
}


bool checkFalsePositive(const bloomFilter& bf, const std::string& url) {}

