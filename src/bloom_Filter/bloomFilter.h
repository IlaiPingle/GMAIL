#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H
#include <vector>
#include <string>
#include <functional>  // For std::hash
#include <memory>  
#include <unordered_set>
#include "hashable.h"
#include "../interfaces/IBloomFilter.h"
using namespace std;
class bloomFilter : public IBloomFilter {
private:
    vector < bool > m_bitArray;
    size_t m_arraySize;
    vector < shared_ptr < hashable >> m_hashFunctions;
    unordered_set < string > m_blackList;                   // To store the real URLs

public:
    bloomFilter();
    bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions);
    bloomFilter(const bloomFilter& other) = default; // Copy constructor
    void add(const std::string& url);
    bool contains(const std::string& url) const;
    bool containsAbsolutely(const std::string& url) const;
    const unordered_set < string >& getBlackList() const;
    void setBlackList(const unordered_set <string>& blackList);
    const vector<bool>& getBitArray() const;
    void setBitArray(const vector<bool>& bitArray);
    
    ~bloomFilter();
};
#endif // BLOOMFILTER_H