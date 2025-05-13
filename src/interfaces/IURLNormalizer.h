#ifndef IURLNORMALIZER_H
#define IURLNORMALIZER_H
#include <string>
using namespace std;

/**
 * @brief Interface for URL normalization.
 * This interface defines the basic operations for normalizing URLs,
 * including standardizing and validating URL formats.
 */
class IURLNormalizer {
public:
    virtual ~IURLNormalizer() = default;
    virtual string normalize(const string& url) = 0;
};
#endif // IURLNORMALIZER_H