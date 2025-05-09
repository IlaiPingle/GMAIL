#include "DeleteCommand.h"


DeleteCommand::DeleteCommand(shared_ptr<IFilterService> filterService, 
                             shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string DeleteCommand::execute(const string& url) {
    if (url.empty()) {
        return ""; // Empty URL check
    }
    if (!m_filterService->contains(url)) {
        return ""; // URL not found in the filter service
    }
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return ""; // Invalid URL format
    }
    
    bool success = m_filterService->remove(standardURL);
    return success ? "" : "Error: Failed to remove URL";
}