#include "CheckCommand.h"
using namespace std;

CheckCommand::CheckCommand(shared_ptr<IFilterService> filterService)
: m_filterService(filterService){}

string CheckCommand::execute(const string& url) {
    if (!m_filterService->contains(url)) {
        return "200 Ok\n\nFalse\n";
    }
    else if (m_filterService->containsAbsolutely(url)) {
        return "200 Ok\n\nTrue True\n";
    } else {
        return "200 Ok\n\nTrue False\n";
    }
    return "400 Bad Request\n"; 
}