#ifndef IURLVALIDATOR_H
#define IURLVALIDATOR_H
#include <string>

class IURLValidator {
public:
    virtual ~IURLValidator() = default;
    virtual std::string standardize(const std::string& url) = 0;
    virtual bool isValid(const std::string& url) = 0;
};
#endif // IURLVALIDATOR_H