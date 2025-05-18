#ifndef DELETECOMMAND_H
#define DELETECOMMAND_H
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
using namespace std;
class DeleteCommand : public ICommand {
    private:
        std::shared_ptr<IFilterService> m_filterService;
    public:
        string execute(const string& params) override;
        DeleteCommand(shared_ptr<IFilterService> filterService);
};
#endif // DELETECOMMAND_H