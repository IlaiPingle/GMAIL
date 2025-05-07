#ifndef ISTORAGESERVICE_H
#define ISTORAGESERVICE_H
#include "IBitArrayStorage.h"
#include "IBlacklistStorage.h"
#include <string>
#include <vector>
#include <unordered_set>
using namespace std;

class IStorageService : public IBitArrayStorage, public IBlacklistStorage {
public:
    virtual ~IStorageService() = default;
    virtual bool fileExistsAndNotEmpty(const string& filename) = 0;
    virtual bool initializeFilter(vector<bool>& bitArray, unordered_set<string>& blacklist) = 0;
};
#endif // ISTORAGESERVICE_H