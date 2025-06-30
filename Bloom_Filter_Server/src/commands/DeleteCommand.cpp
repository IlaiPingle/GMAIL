#include "DeleteCommand.h"

DeleteCommand::DeleteCommand(shared_ptr<IFilterService> filterService)
    : m_filterService(filterService) {}

string DeleteCommand::execute(const string& url) {
    bool success = m_filterService->remove(url);
    return success ? "204 \n" : ""; 
}