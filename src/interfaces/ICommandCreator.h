#ifndef ICOMMANDCREATOR_H
#define ICOMMANDCREATOR_H
#include <memory>
#include "ICommand.h"

/**
 * @brief Interface for command creators.
 * This interface defines the basic operations for creating command objects.
 * It serves as a contract for any command creator implementation, ensuring that
 * the necessary methods are provided.
 */
class ICommandCreator {
public:
    virtual ~ICommandCreator() = default;
    virtual std::shared_ptr<ICommand> createCommand() = 0;
};
#endif // ICOMMANDCREATOR_H