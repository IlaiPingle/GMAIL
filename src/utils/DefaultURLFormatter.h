#ifndef DEFAULTURLFORMATTER_H
#define DEFAULTURLFORMATTER_H
#include "../interfaces/IURLFormatter.h"
#include <string>
using namespace std;

class DefaultURLFormatter : public IURLFormatter {
public:
    string formatURL(const string& url) override;
};
#endif // DEFAULTURLFORMATTER_H