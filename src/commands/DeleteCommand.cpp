#include "DeleteCommand.h"


DeleteCommand::DeleteCommand(shared_ptr<IFilterService> filterService, 
                             shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string DeleteCommand::execute(const string& url) {
    if (url.empty()) {
        return "Bad Request\n"; // Empty URL check
    }
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return "Bad Request\n"; // Invalid URL format
    }
    if (!m_filterService->contains(url)) {
        return "404 Not Found"; // URL not found in the filter service
    
    }
    bool success = m_filterService->remove(standardURL);
    return success ? "204 No Content" : "404  Not Found"; 
}