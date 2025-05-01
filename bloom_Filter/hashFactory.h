#ifndef HASHFACTORY_H
#define HASHFACTORY_H
#include "repeatedHash.h"
#include "hashable.h"
#include <vector>
#include <memory>
using namespace std;
class hashFactory {
public:
 static vector<shared_ptr<hashable>> createHashFunctions ( const vector <size_t> & hashInfo);
};

#endif 