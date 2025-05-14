#include "DefaultURLNormalizer.h"
#include <string>

string DefaultURLNormalizer::normalize(const string& url) {
    string normalizedUrl = url;
    
    // Remove leading whitespace
    size_t start = normalizedUrl.find_first_not_of(" \t\n\r");
    if (start != string::npos) {
        normalizedUrl = normalizedUrl.substr(start);
    } else {
        return ""; // Empty URL after trimming
    }
    
    // Remove trailing whitespace
    size_t end = normalizedUrl.find_last_not_of(" \t\n\r");
    if (end != string::npos) {
        normalizedUrl = normalizedUrl.substr(0, end + 1);
    }
    
    return normalizedUrl;
}