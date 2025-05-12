#ifndef DEFAULTURLNORMALIZER_H
#define DEFAULTURLNORMALIZER_H
#include "../interfaces/IURLNormalizer.h"
#include <string>
using namespace std;

class DefaultURLNormalizer : public IURLNormalizer {
public:
    string normalize(const string& url) override;
};
#endif // DEFAULTURLNORMALIZER_H