#ifndef IURLFORMATTER_H
#define IURLFORMATTER_H
#include <string>
using namespace std;

class IURLFormatter {
public:
    virtual ~IURLFormatter() = default;
    virtual string formatURL(const string& url) = 0;
};
#endif // IURLFORMATTER_H