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
bloomFilter::bloomFilter(int size, int numHashes) {
    bitArray.resize(size, false); // Initialize the bit array with false values
    numHashFunctions = numHashes; // Set the number of hash functions
}

// Add implementation
void bloomFilter::add(const string& key) {
    // Hash the key and set the corresponding bits in the bit array
    for (int i = 0; i < numHashFunctions; i++) {
        int hashValue = hash(key, i);
        bitArray[hashValue] = true;
    }
}

// Contains implementation
bool bloomFilter::contains(const string& key) const {
    // Check if all the bits corresponding to the hashes are set to true
    for (int i = 0; i < numHashFunctions; i++) {
        int hashValue = hash(key, i);
        if (!bitArray[hashValue]) {
            return false; // If any bit is false, the key is definitely not in the filter
        }
    }
    return true; // If all bits are true, the key is possibly in the filter
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