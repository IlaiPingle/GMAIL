#ifndef URLVALIDATOR_H
#define URLVALIDATOR_H
#include <string>
#include "../interfaces/IURLValidator.h"
using namespace std;
class URLValidator : public IURLValidator {
public:
    string standardize(const string& url) override;
    bool isValid(const string& url) override;
};
#endif // URLVALIDATOR_H