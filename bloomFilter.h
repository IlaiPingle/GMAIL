#include <vector>
#include <string>
#include "hashable.h" 
#include <functional> // For std::hash
#include <unordered_set>

class bloomFilter {
private:
    std::vector<bool> m_bitArray;
    size_t m_arraySize;
    int numHashFunctions;
    std::vector<std::function<size_t(const std::string&)>> m_hashFunctions;
    std::unordered_set<std::string> m_blackList; // To store the real URLs
    

public:
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
};