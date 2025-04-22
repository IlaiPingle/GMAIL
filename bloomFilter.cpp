#include <vector>
#include <string>
#include "bloomFilter.h"
#include <functional> // For std::hash
#include <memory>

using namespace std;

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

bool checkFalsePositive(const bloomFilter& bf, const std::string& url) {}