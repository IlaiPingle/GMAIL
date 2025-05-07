#include <algorithm>
#include <string>
#include "URLValidator.h"
using namespace std;

string URLValidator::standardize(const string &url) {
    string standardUrl = url;
    size_t start = standardUrl.find_first_not_of(" \t\n\r");
    if (start != string::npos) {
        standardUrl = standardUrl.substr(start); // Remove leading whitespace
    } else {
        return ""; // Empty URL after trimming
    }
    size_t end = standardUrl.find_last_not_of(" \t\n\r");
    if (end != string::npos) {
        standardUrl = standardUrl.substr(0, end + 1); // Remove trailing whitespace
    }
    // Convert to lowercase
    transform(standardUrl.begin(), standardUrl.end(), standardUrl.begin(),
     [](unsigned char c) {return tolower(c);});
    
    if (standardUrl.find("https://") == 0) {
        standardUrl = standardUrl.substr(8);  // Remove "https://"
    } else if (standardUrl.find("http://") == 0) {
        standardUrl = standardUrl.substr(7);  // Remove "http://"
    }
    if (standardUrl.find("www.") != 0) {
        return ""; // Invalid URL format (not starting with "www.")
    }
    
    return standardUrl;
}

bool URLValidator::isValid(const string& url) {
    return !standardize(url).empty();
}