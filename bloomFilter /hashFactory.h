#ifndef HASHFACTORY_H
#define HASHFACTORY_H

#include "hashable.h"
#include <vector>
#include <memory>
using namespace std;
class HashFactory {
public:
 static vector < shared_ptr < hashable >> HashFactory :: createHashFunctions ( const vector <size_t> & hashInfo);
};

#endif 