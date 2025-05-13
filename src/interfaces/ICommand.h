#ifndef ICOMMAND_H
#define ICOMMAND_H
#include <string>
#include <memory>
using namespace std;

/**
 * @brief Interface for command execution.
 * This interface defines the basic operations for executing commands with parameters.
 * It serves as a contract for any command implementation, ensuring that the necessary
 * methods are provided.
 */
class ICommand {
public:
    virtual ~ICommand() = default;
    virtual string execute(const string& params) = 0;
};
#endif // ICOMMAND_H
