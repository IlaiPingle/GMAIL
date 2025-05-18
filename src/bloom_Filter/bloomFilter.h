#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H
#include <vector>
#include <string>
#include <memory>
#include <cstddef>
#include <unordered_set>
#include "../interfaces/hashable.h"


using namespace std;

class bloomFilter {
    private:
    vector <bool> m_bitArray;
    size_t m_arraySize;
    vector<shared_ptr<hashable>> m_hashFunctions;
    unordered_set<string> m_blackList;
    
    public:
    // Constructors with proper initialization
    bloomFilter();
    ~bloomFilter() = default;
    bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions);
    bool add(const string& url) ;
    bool contains(const string& url) const ;
    bool containsAbsolutely(const string& url) const ;
    bool remove(const string& url) ;
    const unordered_set<string>& getBlackList() const ;
    const vector<shared_ptr<hashable>> getHashFunctions() const ;
    const vector<bool>& getBitArray() const ;
    void setBitArray(const vector<bool>& bitArray) ;
    void setBlackList(const unordered_set<string>& blackList) ;
    void setHashFunctions(const vector<shared_ptr<hashable>>& hashFunctions) ;
        
    };
    #endif // BLOOMFILTER_H