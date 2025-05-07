#ifndef IFILTERSERVICE_H
#define IFILTERSERVICE_H
#include <string>
using namespace std;

class IFilterService {
public:
    virtual ~IFilterService() = default;
    virtual bool add(const string& url) = 0;
    virtual bool contains(const string& url) = 0;
    virtual bool containsAbsolutely(const string& url) = 0;
    //virtual bool remove(const string& url) = 0;
};
#endif // IFILTERSERVICE_H