#include <vector>
#include <string>
#include "hashable.h" 
#include <functional> // For std::hash
#include <unordered_set>

using namespace std;
class bloomFilter {
private:
    vector < bool > m_bitArray;
    size_t m_arraySize;
    vector < shared_ptr < hashable >> m_hashFunctions;
    unordered_set < string > m_blackList;                   // To store the real URLs

public:
    bloomFilter(size_t size, const vector<shared_ptr<hashable>>& hashFunctions);
    void add(const std::string& url);
    bool contains(const std::string& url) const;
    bool containsAbsolutely(const std::string& url) const;
    const unordered_set < string >& bloomFilter::getBlackList() const;
    void bloomFilter::setBlackList(const std::vector<std::string>& newBlacklistedUrls);
    void saveToFile(const std::string& filename) const;
    void loadFromFile(const std::string& filename);
    bool fileExists(const std::string& filename) const;
    
    bool checkFalsePositive(const bloomFilter& bl, const std::string& url);
};