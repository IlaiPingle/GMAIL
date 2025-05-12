#include "DefaultURLFormatter.h"
#include <algorithm>

string DefaultURLFormatter::formatURL(const string& url) {
    string formattedUrl = url;
    // Convert to lowercase
    transform(formattedUrl.begin(), formattedUrl.end(), formattedUrl.begin(),
              [](unsigned char c) { return tolower(c); });
    
    // Handle protocol prefixes
    if (formattedUrl.find("https://") == 0) {
        formattedUrl = formattedUrl.substr(8);  // Remove "https://"
    } else if (formattedUrl.find("http://") == 0) {
        formattedUrl = formattedUrl.substr(7);  // Remove "http://"
    }
    
    return formattedUrl;
}