#include "bloomFilter.h"
#include <algorithm>
#include <filesystem>


// Default constructor
bloomFilter::bloomFilter() : m_arraySize(0) {}
// Constructor
bloomFilter::bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions)
: m_bitArray(size, false), m_arraySize(size), m_hashFunctions(hashFunctions) {}

bloomFilter::bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions, shared_ptr<IBitVector> bitVector)
: m_bitArray(size, false), m_arraySize(size), m_hashFunctions(hashFunctions), m_bitVector(bitVector) {
    if (m_bitVector && size > 0) {
        // Initialize the bit vector if needed
        for (size_t i = 0; i < size; ++i) {
            if (m_bitArray[i]) {
                m_bitVector->set(i);
            }
        }
    }
}

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
    }
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
        return false; // No hash functions, cannot check for existence
    }
    for (const auto& func : m_hashFunctions) {
        if (!func) {
            continue; // Skip null hash functions
        }
        size_t index = (*func)(url) % m_arraySize;
        if (!m_bitArray[index]){
            return false;
        }
    }
    return true;
}

bool bloomFilter::containsAbsolutely(const string& url) const {
    if (contains(url)) {
        // Check if the URL is in the blacklist
        return m_blackList.find(url) != m_blackList.end();
    }
    return false;
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