#ifndef IFILTERSERVICE_H
#define IFILTERSERVICE_H
#include <string>
using namespace std;

/**
 * @brief Interface for a filter service.
 * This interface defines the basic operations for managing a filter service,
 * including adding, checking, and removing URLs from the filter.
 */
class IFilterService {
public:
    virtual ~IFilterService() = default;
    virtual bool add(const string& url) = 0;
    virtual bool contains(const string& url) = 0;
    virtual bool containsAbsolutely(const string& url) = 0;
    virtual bool remove(const string& url) = 0;
    virtual bool initialize() = 0;
};
#endif // IFILTERSERVICE_H