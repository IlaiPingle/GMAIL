#include "CheckCommand.h"
using namespace std;

CheckCommand::CheckCommand(shared_ptr<IFilterService> filterService)
: m_filterService(filterService){}

string CheckCommand::execute(const string& url) {
    if (!m_filterService->contains(url)) {
        return "200 False\n";
    }
    else if (m_filterService->containsAbsolutely(url)) {
        return "200 True\n";
    } else {
        return "200 False\n";
    }
    return "400 \n"; 
}