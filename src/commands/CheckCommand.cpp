#include "CheckCommand.h"
using namespace std;

CheckCommand::CheckCommand(shared_ptr<IFilterService> filterService, 
                          shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string CheckCommand::execute(const string& url) {
    if (url.empty()) {
        return "false"; // Empty URL check
    }
    
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return "false"; // Invalid URL format
    }
    
    if (!m_filterService->contains(standardURL)) {
        return "false";
    }
    else if (m_filterService->containsAbsolutely(standardURL)) {
        return "true true";
    } else {
        return "true false";
    }
}