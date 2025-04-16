#include <vector>
#include <string>
#include"bloomFilter.h"
#include <functional> // For std::hash
using namespace std;
class bloomFilter{
private:
    int my_size;
    vector<function<int(const string&)>> my_hashFunctions; // Vector of hash functions
    vector<bool> bitArray; // Bit array to represent the bloom filter
public:
    // 1. Constructor: Initializes the bloom filter with a given size and number of hash functions.
    bloomFilter(int size, const vector<function<int(const string&)>>& hashFunctions) {
        my_size = size;
        my_hashFunctions = hashFunctions;
        bitArray.resize(size, false); // Initialize the bit array with false values
    }
    // 2. Destructor: Cleans up any allocated resources.
    ~bloomFilter(){}
    // 3. Add: Adds an URL to the bloom filter.
    void add(const string &URL){
        // Hash the URL and set the corresponding bits in the bit array
        for (const auto& hashFunction : my_hashFunctions) {
            int hashValue = hashFunction(URL) % my_size;
            bitArray[hashValue] = true;
        }
    }
    // 4. Contains: Checks if an URL is possibly in the bloom filter.
    bool contains(const string &URL) {
        // Check if all the bits corresponding to the hashes are set to true
        for (const auto& hashFunction : my_hashFunctions) {
            int hashValue = hashFunction(URL) % my_size;
            if (!bitArray[hashValue]) {
                return false; // If any bit is false, the URL is definitely not in the filter
            }
        }
        return true; // If all bits are true, the URL is possibly in the filter
    }  

};
