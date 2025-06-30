#include "AddCommand.h"

AddCommand::AddCommand(shared_ptr<IFilterService> filterService)
    : m_filterService(filterService){}

string AddCommand::execute(const string& url) {
    bool success = m_filterService->add(url);
    return success ? "201 \n" : "500";  // Internal Server Error
}