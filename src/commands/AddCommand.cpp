#include "AddCommand.h"


AddCommand::AddCommand(shared_ptr<IFilterService> filterService, shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

string AddCommand::execute(const string& url) {
    if (url.empty()) {
        return "400 Bad Request\n";  // Empty URL check
    }
    string standardURL = m_urlValidator->standardize(url);
    if (standardURL.empty()) {
        return "400 Bad Request\n";  // Invalid URL format
    }
    bool success = m_filterService->add(standardURL);
    return success ? "201 Created\n" : "404 Not Found\n";  // URL already exists
}