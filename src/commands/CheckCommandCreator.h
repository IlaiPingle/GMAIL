#ifndef CHECKCOMMANDCREATOR_H
#define CHECKCOMMANDCREATOR_H
#include "../interfaces/ICommandCreator.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"
#include <memory>

class CheckCommandCreator : public ICommandCreator {
private:
    std::shared_ptr<IFilterService> m_filterService;
    std::shared_ptr<IURLValidator> m_urlValidator;
    
public:
    CheckCommandCreator(std::shared_ptr<IFilterService> filterService, 
                       std::shared_ptr<IURLValidator> urlValidator);
                       
    std::shared_ptr<ICommand> createCommand() override;
};
#endif // CHECKCOMMANDCREATOR_H