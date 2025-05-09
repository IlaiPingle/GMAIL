#ifndef DELETECOMMAND_H
#define DELETECOMMAND_H
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
#include "../interfaces/IURLValidator.h"
using namespace std;
class DeleteCommand : public ICommand {
    private:
        std::shared_ptr<IFilterService> m_filterService;
        std::shared_ptr<IURLValidator> m_urlValidator;
    public:
        string execute(const string& params) override;
        DeleteCommand(shared_ptr<IFilterService> filterService, shared_ptr<IURLValidator> urlValidator);
};
#endif // DELETECOMMAND_H