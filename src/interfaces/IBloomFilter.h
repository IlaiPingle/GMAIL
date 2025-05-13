#ifndef IBLOOMFILTER_H
#define IBLOOMFILTER_H
#include <string>
#include <vector>
#include <unordered_set>
using namespace std;
/**
 * @brief Interface for a Bloom Filter.
 * This interface defines the basic operations for a Bloom Filter, including adding,
 * checking, and removing URLs, as well as managing the internal state of the filter.
 */
class IBloomFilter {
public:
    IBloomFilter() = default;
    virtual ~IBloomFilter() = default;
    virtual bool add(const string& url) = 0;
    virtual bool contains(const string& url) const = 0;
    virtual bool remove(const string& url) = 0;
    virtual bool containsAbsolutely(const string& url) const = 0;
    virtual const unordered_set<string>& getBlackList() const = 0;
    virtual void setBlackList(const unordered_set<string>& blackList) = 0;
    virtual const vector<bool>& getBitArray() const = 0;
    virtual void setBitArray(const vector<bool>& bitArray) = 0;
};

#endif // IBLOOMFILTER_H