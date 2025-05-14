#include "DefaultURLFormatter.h"
#include "DefaultURLNormalizer.h"
#include "URLValidator.h"
#include <memory>

URLValidator::URLValidator()
    : m_formatter(make_shared<DefaultURLFormatter>()), m_normalizer(make_shared<DefaultURLNormalizer>()) {}

URLValidator::URLValidator(shared_ptr<IURLFormatter> formatter, shared_ptr<IURLNormalizer> normalizer)
    : m_formatter(formatter ? formatter : make_shared<DefaultURLFormatter>()),
     m_normalizer(normalizer ? normalizer : make_shared<DefaultURLNormalizer>()) {}

string URLValidator::standardize(const string &url) {
    string formattedURL =m_formatter->formatURL(url);
    return m_normalizer->normalize(formattedURL);
}

bool URLValidator::isValid(const string& url) {
    return !standardize(url).empty();
}