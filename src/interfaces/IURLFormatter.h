#ifndef IURLFORMATTER_H
#define IURLFORMATTER_H
#include <string>
using namespace std;

/**
 * @brief Interface for URL formatting.
 * This interface defines the basic operations for formatting URLs,
 * including standardizing and validating URL formats.
 */
class IURLFormatter {
public:
    virtual ~IURLFormatter() = default;
    virtual string formatURL(const string& url) = 0;
};
#endif // IURLFORMATTER_H