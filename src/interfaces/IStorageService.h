#ifndef ISTORAGESERVICE_H
#define ISTORAGESERVICE_H
#include "IBitArrayStorage.h"
#include "IBlacklistStorage.h"
#include <string>
#include <vector>
#include <unordered_set>
using namespace std;

/**
 * @brief Interface for a storage service.
 * This interface defines the basic operations for managing storage of bit arrays
 * and blacklists, including checking file existence, initializing filters, and
 * loading and saving data.
 */
class IStorageService : public IBitArrayStorage, public IBlacklistStorage {
public:
    virtual ~IStorageService() = default;
    virtual bool fileExistsAndNotEmpty(const string& filename) = 0;
    virtual bool initializeFilter(vector<bool>& bitArray, unordered_set<string>& blacklist) = 0;
};
#endif // ISTORAGESERVICE_H