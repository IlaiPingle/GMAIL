#include "DefaultURLNormalizer.h"
#include <string>

string DefaultURLNormalizer::normalize(const string& url){
    string formatedURL =  DefaultURLFormatter::formatURL(url);
    
    // Remove leading whitespace
    size_t start = formatedURL.find_first_not_of(" \t\n\r");
    if (start != string::npos) {
        formatedURL = formatedURL.substr(start);
    } else {
        return ""; // Empty URL after trimming
    }
    
    // Remove trailing whitespace
    size_t end = formatedURL.find_last_not_of(" \t\n\r");
    if (end != string::npos) {
        formatedURL = formatedURL.substr(0, end + 1);
    }
    
    return formatedURL;
}