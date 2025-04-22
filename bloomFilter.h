#include <vector>
#include <string>

class bloomFilter {
private:
    std::vector<bool> bitArray;
    int numHashFunctions;
    

    int hash(const std::string& key, int seed) const;

public:
    bloomFilter(int size, int numHashes);
    void add(const std::string& key);
    bool contains(const std::string& key) const;
};