#ifndef IURLVALIDATOR_H
#define IURLVALIDATOR_H
#include <string>
using namespace std;
/**
 * @brief Interface for URL validation.
 * This interface defines the basic operations for validating and standardizing URLs.
 * It serves as a contract for any URL validator implementation, ensuring that
 * the necessary methods are provided.
 */
class IURLValidator {
public:
    virtual ~IURLValidator() = default;
    virtual string standardize(const string& url) = 0;
    virtual bool isValid(const string& url) = 0;
};
#endif // IURLVALIDATOR_H