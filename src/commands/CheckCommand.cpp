#include "CheckCommand.h"
using namespace std;

CheckCommand::CheckCommand(shared_ptr<IFilterService> filterService, 
                          shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string CheckCommand::execute(const string& url) {
    if (url.empty()) {
        return "400 Bad Request\n"; // Empty URL check
    }
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return "400 Bad Request\n"; // Invalid URL format
    }
    if (!m_filterService->contains(standardURL)) {
        return "200 Ok\n\nFalse\n";
    }
    else if (m_filterService->containsAbsolutely(standardURL)) {
        return "200 Ok\n\nTrue True\n";
    } else {
        return "200 Ok\n\nTrue False\n";
    }
}