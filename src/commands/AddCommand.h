#ifndef ADDCOMMAND_H
#define ADDCOMMAND_H
#include "../interfaces/ICommand.h"
#include "../interfaces/IFilterService.h"
#include <memory>
using namespace std;
class AddCommand : public ICommand {
    private:
        shared_ptr<IFilterService> m_filterService;
    
    public:
        AddCommand(shared_ptr<IFilterService> filterService);
        string execute(const string& url) override;
};
#endif // ADDCOMMAND_H