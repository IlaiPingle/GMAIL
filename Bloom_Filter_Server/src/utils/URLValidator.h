#ifndef URLVALIDATOR_H
#define URLVALIDATOR_H
#include <string>
#include <memory>
# include "DefaultURLNormalizer.h"
using namespace std;
class URLValidator  {
    private:
    URLValidator() = delete;
    
    public:
    static bool isValid(const string& url);
    static bool isValidURL(string& url);
};
#endif // URLVALIDATOR_H