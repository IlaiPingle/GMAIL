#include <vector>
#include <string>
#include "hashable.h" 
#include <memory>

class bloomFilter {
private:
    std::vector<bool> m_bitArray;
    size_t m_arraySize;
    std::vector<std::shared_ptr<hashable>> m_hashFunctions;
    

public:
    void add(const std::string& key);
    bool contains(const std::string& key) const;
    void saveToFile(const std::string& filename) const;
    void loadFromFile(const std::string& filename);
    bool fileExists(const std::string& filename) const;
    bloomFilter(size_t size, const std::vector<std::shared_ptr<hashable>>& hashFuncs);
    bool checkFalsePositive(const bloomFilter& bf, const std::string& url)  const;
};