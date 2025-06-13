
#include "URLValidator.h"
#include <regex>
#include <string>
/*
bool URLValidator::isValid(const string& url) {
return !standardize(url).empty();
}*/

bool URLValidator::isValidURL(string& url) {
    if(!DefaultURLNormalizer::normalize(url)){
        return false;
    }
    //  check for optional http/https prefix
    //  check for domain name
    //  domain suffix :more than 2 characters
    //  optional port number
    //  optional path/parameters
    static const regex URLFrmat(
        R"(^(https?://)?([A-Za-z0-9-]+\.)+[A-Za-z]{2,}(:\d{1,5})?(/\S*)?$)",
        regex::icase
        | regex::optimize
    );
    return regex_match(url, URLFrmat);
}