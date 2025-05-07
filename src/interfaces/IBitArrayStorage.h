#ifndef IBITARRAYSTORAGE_H
#define IBITARRAYSTORAGE_H
#include <vector>

class IBitArrayStorage {
public:
    virtual ~IBitArrayStorage() = default;
    virtual bool saveBitArray(const std::vector<bool>& bitArray) = 0;
    virtual bool loadBitArray(std::vector<bool>& bitArray) = 0;
};
#endif // IBITARRAYSTORAGE_H