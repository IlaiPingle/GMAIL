#ifndef ADDCOMMANDCREATOR_H
#define ADDCOMMANDCREATOR_H
#include "../interfaces/ICommandCreator.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"
#include <memory>

class AddCommandCreator : public ICommandCreator {
private:
    std::shared_ptr<IFilterService> m_filterService;
    std::shared_ptr<IURLValidator> m_urlValidator;
    
public:
    AddCommandCreator(std::shared_ptr<IFilterService> filterService, 
                     std::shared_ptr<IURLValidator> urlValidator);
                     
    std::shared_ptr<ICommand> createCommand() override;
};
#endif // ADDCOMMANDCREATOR_H