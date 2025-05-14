#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H
#include <vector>
#include <string>
#include <memory>
#include <cstddef>
#include <unordered_set>
#include "hashable.h"
#include "../interfaces/IBloomFilter.h"
#include "../interfaces/IBitVector.h"

using namespace std;

class bloomFilter : public IBloomFilter {
private:
    vector<bool> m_bitArray;
    shared_ptr<IBitVector> m_bitVector;
    size_t m_arraySize;
    vector<shared_ptr<hashable>> m_hashFunctions;
    unordered_set<string> m_blackList;

public:
    // Constructors with proper initialization
    bloomFilter();
    ~bloomFilter() override = default;
    bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions);
    bloomFilter(size_t size, 
                const vector<shared_ptr<hashable>>& hashFunctions,
                shared_ptr<IBitVector> bitVector);
    
    // Interface implementations
    bool add(const string& url) override;
    bool contains(const string& url) const override;
    bool containsAbsolutely(const string& url) const override;
    bool remove(const string& url) override;
    
    // Accessor methods
    const unordered_set<string>& getBlackList() const override;
    void setBlackList(const unordered_set<string>& blackList) override;
    const vector<bool>& getBitArray() const override;
    void setBitArray(const vector<bool>& bitArray) override;
    void setHashFunctions(const vector<shared_ptr<hashable>>& hashFunctions) override;
};
#endif // BLOOMFILTER_H