#ifndef IBLACKLISTSTORAGE_H
#define IBLACKLISTSTORAGE_H
#include <string>
#include <unordered_set>

class IBlacklistStorage {
public:
    virtual ~IBlacklistStorage() = default;
    virtual bool saveBlacklist(const std::unordered_set<std::string>& blacklist) = 0;
    virtual bool loadBlacklist(std::unordered_set<std::string>& blacklist) = 0;
    virtual bool removeFromBlacklist(const std::string& url) = 0;
    virtual bool isInBlacklist(const std::string& url) = 0;
};
#endif // IBLACKLISTSTORAGE_H