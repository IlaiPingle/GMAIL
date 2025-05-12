#ifndef IURLNORMALIZER_H
#define IURLNORMALIZER_H
#include <string>
using namespace std;

class IURLNormalizer {
public:
    virtual ~IURLNormalizer() = default;
    virtual string normalize(const string& url) = 0;
};
#endif // IURLNORMALIZER_H