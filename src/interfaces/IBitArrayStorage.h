#ifndef IBITARRAYSTORAGE_H
#define IBITARRAYSTORAGE_H
#include <vector>
using namespace std;
class IBitArrayStorage {
public:
    virtual ~IBitArrayStorage() = default;
    virtual bool saveBitArray(const vector<bool>& bitArray) = 0;
    virtual bool loadBitArray(vector<bool>& bitArray) = 0;
};
#endif // IBITARRAYSTORAGE_H