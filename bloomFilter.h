#ifndef BLOOMFILTER_H
#define BLOOMFILTER_H
#include <vector>
#include <string>
#include "hashable.h" 
#include <functional> // For std::hash
#include <unordered_set>
#include <cmath> // For std::log and std::round

class bloomFilter {
private:
    std::vector<bool> m_bitArray;
    size_t m_arraySize;
    int numHashFunctions;
    std::vector<std::function<size_t(const std::string&)>> m_hashFunctions;
    std::unordered_set<std::string> m_blackList; // To store the real URLs
    

public:
    static size_t calculateOptimalSize(size_t numElements, double falsePositiveRate){
        return static_cast<size_t>(-numElements * log(falsePositiveRate) / (log(2) * log(2)));
    }
    static size_t calculateOptimalHashFunctions(size_t size, size_t numElements){
        return static_cast<size_t>(round((size / numElements) * log(2)));
    }
    bloomFilter(size_t size, size_t numHashes);
    void add(const std::string& url);
    bool contains(const std::string& url) const;
    void saveToFile(const std::string& filename) const;
    void loadFromFile(const std::string& filename);
    bool fileExists(const std::string& filename) const;
    bloomFilter(size_t size, const std::vector<std::function<size_t(const std::string&)>>& hashFuncs);
    bool isFalsePositive(const std::string& url) const;
    bool checkFalsePositive(const bloomFilter& bl, const std::string& url);
    void saveBlackListToFile(const std::string& filename) const;
    void loadBlackListFromFile(const std::string& filename);
    ~bloomFilter() = default; // Default destructor
    void clear();
};

#endif // BLOOMFILTER_H