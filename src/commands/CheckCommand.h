#ifndef CHECKCOMMAND_H
#define CHECKCOMMAND_H
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"
#include <memory>
using namespace std;
class CheckCommand : public ICommand {
    private:
        shared_ptr<IFilterService> m_filterService;
        shared_ptr<IURLValidator> m_urlValidator;
    
    public:
        CheckCommand(shared_ptr<IFilterService> filterService, shared_ptr<IURLValidator> urlValidator);
        string execute(const string& params) override;
};
#endif // CHECKCOMMAND_H