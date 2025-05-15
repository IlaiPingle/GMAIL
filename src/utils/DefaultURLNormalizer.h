#ifndef DEFAULTURLNORMALIZER_H
#define DEFAULTURLNORMALIZER_H
#include "../utils/DefaultURLFormatter.h"
#include <string>
using namespace std;

class DefaultURLNormalizer  {
    public:
    static string normalize(const string& url) ;
};
#endif // DEFAULTURLNORMALIZER_H