#ifndef IBLACKLISTSTORAGE_H
#define IBLACKLISTSTORAGE_H
#include <string>
#include <unordered_set>
using namespace std;   
/**
 * @brief Interface for a blacklist storage.
 * This interface defines the basic operations for managing a blacklist of URLs,
 * including saving, loading, removing, and checking URLs in the blacklist.
 */ 
class IBlacklistStorage {
public:
    virtual ~IBlacklistStorage() = default;
    virtual bool saveBlacklist(const unordered_set<string>& blacklist) = 0;
    virtual bool loadBlacklist(unordered_set<string>& blacklist) = 0;
    virtual bool removeFromBlacklist(const string& url) = 0;
    virtual bool configFromFile(string& configLine) = 0;
    virtual bool isInBlacklist(const string& url) = 0;
};
#endif // IBLACKLISTSTORAGE_H