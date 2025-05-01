#include "hashFactory.h"


using namespace std;

// function which creates the hash functions for now it creates only repeatedHash
// it takes an array of the repeats each hash function should do.
// returns a vector of hashable functions.
vector<shared_ptr<hashable>> hashFactory::createHashFunctions( const vector <size_t> & hashInfo) {
    vector < shared_ptr< hashable > > hashFunctions; 
    for(size_t reps : hashInfo) {
        shared_ptr<repeatedHash> hash = make_shared<repeatedHash>(reps);
        hashFunctions.push_back(hash);
    }
    return hashFunctions;
}
