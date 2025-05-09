#include "AddCommand.h"


AddCommand::AddCommand(shared_ptr<IFilterService> filterService, shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string AddCommand::execute(const string& url) {
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return ""; // Invalid URL format
    }
    
    bool success = m_filterService->add(standardURL);
    return success ? "" : "Error: Failed to add URL";
}