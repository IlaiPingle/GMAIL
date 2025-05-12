#include "AddCommandCreator.h"
#include "AddCommand.h"

AddCommandCreator::AddCommandCreator(std::shared_ptr<IFilterService> filterService,
                                   std::shared_ptr<IURLValidator> urlValidator)
    : m_filterService(filterService), m_urlValidator(urlValidator) {}

std::shared_ptr<ICommand> AddCommandCreator::createCommand() {
    return std::make_shared<AddCommand>(m_filterService, m_urlValidator);
}