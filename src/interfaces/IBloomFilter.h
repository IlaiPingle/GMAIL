#ifndef IBLOOMFILTER_H
#define IBLOOMFILTER_H
#include <string>
#include <vector>
#include <unordered_set>

class IBloomFilter {
public:
    IBloomFilter() = default;
    virtual ~IBloomFilter() = default;
    virtual bool add(const std::string& url) = 0;
    virtual bool contains(const std::string& url) const = 0;
    virtual bool remove(const std::string& url) = 0;
    virtual bool containsAbsolutely(const std::string& url) const = 0;
    virtual const std::unordered_set<std::string>& getBlackList() const = 0;
    virtual void setBlackList(const std::unordered_set<std::string>& blackList) = 0;
    virtual const std::vector<bool>& getBitArray() const = 0;
    virtual void setBitArray(const std::vector<bool>& bitArray) = 0;
};

#endif // IBLOOMFILTER_H