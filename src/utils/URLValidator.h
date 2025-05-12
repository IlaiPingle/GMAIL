#ifndef URLVALIDATOR_H
#define URLVALIDATOR_H
#include <string>
#include <memory>
#include "../interfaces/IURLValidator.h"
#include "../interfaces/IURLFormatter.h"
#include "../interfaces/IURLNormalizer.h"
using namespace std;
class URLValidator : public IURLValidator {
    private:
    shared_ptr<IURLFormatter> m_formatter;
    shared_ptr<IURLNormalizer> m_normalizer;
public:
    URLValidator();
    URLValidator(shared_ptr<IURLFormatter> formatter, shared_ptr<IURLNormalizer> normalizer);
    string standardize(const string& url) override;
    bool isValid(const string& url) override;
};
#endif // URLVALIDATOR_H