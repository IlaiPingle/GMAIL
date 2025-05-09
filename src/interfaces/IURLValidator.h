#ifndef IURLVALIDATOR_H
#define IURLVALIDATOR_H
#include <string>
using namespace std;
class IURLValidator {
public:
    virtual ~IURLValidator() = default;
    virtual string standardize(const string& url) = 0;
    virtual bool isValid(const string& url) = 0;
};
#endif // IURLVALIDATOR_H