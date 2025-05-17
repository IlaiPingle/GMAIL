#include "bloomFilter.h"
#include <algorithm>
#include <filesystem>

#include <iostream> // For debugging ****


// Default constructor
bloomFilter::bloomFilter() : m_arraySize(0) {}


// Constructor
bloomFilter::bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions)
: m_bitArray(size, false), m_arraySize(size), m_hashFunctions(hashFunctions) {}


bool bloomFilter::add(const string& url) {
    if (m_arraySize == 0 || m_hashFunctions.empty()) {
        return false; // Cannot add to an empty filter
    }
    for (const auto& func : m_hashFunctions) {
        if (!func) {
            continue; // Skip null hash functions
        }
        size_t index = (*func)(url) % m_arraySize;
        m_bitArray[index] = true;
        cout << "Hash function index: " << m_bitArray[index] << " "; // Debug message *****
    }
    cout << "/n"; // Debug message *****
    
    m_blackList.insert(url); // Store the real URL in the blacklist
    return true; // Return true to indicate that the URL was added
}

bool bloomFilter::remove(const string& url) {
    // Check if the URL is in the blacklist
    auto result = m_blackList.erase(url);
    return result > 0; // Return true if the URL was removed, false otherwise
}


bool bloomFilter::contains(const string& url) const {
    if (m_hashFunctions.empty() || m_arraySize == 0) {
        cout << "No hash functions or bit array size is zero." << endl; // *** Debug message ****
        return false; // No hash functions, cannot check for existence
    }
    for (const auto& func : m_hashFunctions) {
        if (!func) {
            continue; // Skip null hash functions
        }
        size_t index = (*func)(url) % m_arraySize;
        cout << "Hash function index: " << m_bitArray[index] << " ";  // **** Debug message *****
        if (!m_bitArray[index]){
            return false;
        }
        cout << "/n"; // **** Debug message *****
    }
    return true;
}

bool bloomFilter::containsAbsolutely(const string& url) const {
        return m_blackList.find(url) != m_blackList.end();
}


const unordered_set < string >& bloomFilter::getBlackList() const {
    return m_blackList;
}

void bloomFilter::setBlackList(const unordered_set <string>& blackList) {
    m_blackList = blackList;

}
const vector<bool>& bloomFilter::getBitArray() const {
    return m_bitArray;
}

void bloomFilter::setBitArray(const vector<bool>& bitArray) {
    m_bitArray = bitArray;
    m_arraySize = bitArray.size();

}
void bloomFilter::setHashFunctions(const vector<shared_ptr<hashable>>& hashFunctions) {
    m_hashFunctions = hashFunctions;
}

const vector<shared_ptr<hashable>>  bloomFilter::getHashFunctions() const {
    return m_hashFunctions;
}
