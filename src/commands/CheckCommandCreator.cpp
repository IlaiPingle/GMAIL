#include "CheckCommandCreator.h"
#include "CheckCommand.h"

CheckCommandCreator::CheckCommandCreator(std::shared_ptr<IFilterService> filterService,
                                       std::shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

std::shared_ptr<ICommand> CheckCommandCreator::createCommand() {
    return std::make_shared<CheckCommand>(m_filterService, m_urlValidator);
}