#ifndef ADDCOMMAND_H
#define ADDCOMMAND_H
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"
#include <memory>
using namespace std;
class AddCommand : public ICommand {
private:
    shared_ptr<IFilterService> m_filterService;
    shared_ptr<IURLValidator> m_urlValidator;

public:
    AddCommand(shared_ptr<IFilterService> filterService,
               shared_ptr<IURLValidator> urlValidator);
    
    string execute(const string& url) override;
};
#endif // ADDCOMMAND_H