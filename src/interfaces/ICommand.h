#ifndef ICOMMAND_H
#define ICOMMAND_H
#include <string>
#include <memory>
using namespace std;

class ICommand {
public:
    virtual ~ICommand() = default;
    virtual string execute(const string& params) = 0;
};
#endif // ICOMMAND_H
