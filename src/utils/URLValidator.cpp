#include "DefaultURLFormatter.h"
#include "DefaultURLNormalizer.h"
#include "URLValidator.h"
#include <memory>


bool URLValidator::isValid(const string& url) {
    return !standardize(url).empty();
}
string URLValidator::standardize(const string &url){
    return DefaultURLNormalizer::normalize(url);
}