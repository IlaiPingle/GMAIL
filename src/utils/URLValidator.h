#ifndef URLVALIDATOR_H
#define URLVALIDATOR_H
#include <string>
#include <memory>
#include "../interfaces/IURLValidator.h"
#include "../interfaces/IURLFormatter.h"
#include "../interfaces/IURLNormalizer.h"
using namespace std;
class URLValidator  {
    private:
    URLValidator() = delete;
    
    public:
    static string standardize(const string& url) ;
    static bool isValid(const string& url);
};
#endif // URLVALIDATOR_H