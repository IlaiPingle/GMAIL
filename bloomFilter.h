#include <vector>
#include <string>
#include "hashable.h" 
#include <memory>

class bloomFilter {
private:
    std::vector<bool> m_bitArray;
    size_t m_arraySize;
    std::vector<hashable*> m_hashFunctions;
    size_t numHashFunctions;
    
    

    int hash(const std::string& key, int seed) const;

public:
    bloomFilter(size_t size, const std::vector<std::shared_ptr<hashable>>& hashFuncs);
    void bloomFilter::add(const std::string& url);
    bool bloomFilter::contains(const std::string& url) const;
    bool checkFalsePositive(const bloomFilter& bf, const std::string& url)  const;
};