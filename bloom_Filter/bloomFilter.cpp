#include "bloomFilter.h"
#include <algorithm>
using namespace std;


// Constructor
bloomFilter::bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions)
: m_bitArray(size, false), m_arraySize(size), m_hashFunctions(hashFunctions) {}


void bloomFilter::add(const string& url) {
    for (const auto& func : m_hashFunctions) {
        size_t index = (*func)(url) % m_arraySize;
        m_bitArray[index] = true;
    }
    m_blackList.insert(url); // Store the real URL in the blacklist
    
}


bool bloomFilter::contains(const string& url) const {
    for (const auto& func : m_hashFunctions) {
        size_t index = (*func)(url) % m_arraySize;
        if (!m_bitArray[index]){
            return false;
        }
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