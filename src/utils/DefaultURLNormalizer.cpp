#include "DefaultURLNormalizer.h"
#include <string>

bool DefaultURLNormalizer::normalize(string& url){
    // Remove leading whitespace
    size_t start = url.find_first_not_of(" \t\n\r");
    if (start != string::npos) {
        url = url.substr(start);
    } else {
        return false; // Empty URL after trimming
    }
    // Remove trailing whitespace
    size_t end = url.find_last_not_of(" \t\n\r");
    if (end != string::npos) {
        url = url.substr(0, end + 1);
    }
    return true;
}