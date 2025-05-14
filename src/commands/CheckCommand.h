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
    
    public:
        CheckCommand(shared_ptr<IFilterService> filterService);
        string execute(const string& params) override;
};
#endif // CHECKCOMMAND_H