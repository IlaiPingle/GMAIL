#include <vector>
#include <string>
#include "hashable.h" 
#include <memory>

class bloomFilter {
private:
    std::vector<bool> m_bitArray;
    size_t m_arraySize;
    std::vector<std::shared_ptr<hashable>> m_hashFunctions;
    size_t numHashFunctions;

public:
    bloomFilter(size_t size, const std::vector<std::shared_ptr<hashable>>& hashFuncs);
    void bloomFilter::add(const std::string& url);
    bool bloomFilter::contains(const std::string& url) const;
    bool checkFalsePositive(const bloomFilter& bf, const std::string& url)  const;
};