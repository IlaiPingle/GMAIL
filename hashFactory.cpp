#include "hashFactory.h"
#include "repeatedHash.h"
#include "hashable.h"
#include <sstream>
using namespace std;

// function which creates the hash functions for now it creates only repeatedHash
// it takes the string which contains the number of times to repeat the hash function and
// returns a vector of hashable functions.
vector < shared_ptr < hashable >> HashFactory :: createHashFunctions (const string& hashInfoStr) {
    vector < shared_ptr< hashable > > hashFunctions; 
    istringstream nextWord(hashInfoStr);
    size_t reps;
    while (nextWord >> reps) {
        shared_ptr<repeatedHash> hash = make_shared<repeatedHash>(reps);
        hashFunctions.push_back(hash);
    }
    return hashFunctions;
}
