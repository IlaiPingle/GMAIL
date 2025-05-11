#ifndef ICOMMANDCREATOR_H
#define ICOMMANDCREATOR_H
#include <memory>
#include "ICommand.h"

class ICommandCreator {
public:
    virtual ~ICommandCreator() = default;
    virtual std::shared_ptr<ICommand> createCommand() = 0;
};
#endif // ICOMMANDCREATOR_H