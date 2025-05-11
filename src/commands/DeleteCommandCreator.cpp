#include "DeleteCommandCreator.h"
#include "DeleteCommand.h"

DeleteCommandCreator::DeleteCommandCreator(std::shared_ptr<IFilterService> filterService,
                                         std::shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

std::shared_ptr<ICommand> DeleteCommandCreator::createCommand() {
    return std::make_shared<DeleteCommand>(m_filterService, m_urlValidator);
}