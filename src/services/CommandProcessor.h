#ifndef COMMAND_PROCESSOR_H
#define COMMAND_PROCESSOR_H

#include <string>
#include <memory>
#include "../interfaces/IBloomFilter.h"
#include "../interfaces/IStorageService.h"
#include "../ioHandling/inputManager.h"
#include "../commands/CommandFactory.h"
using namespace std;
/**
 * @class CommandProcessor
 * @brief A class that processes commands related to URL filtering.
 *
 * This class responsible for taking user request and executes the corresponding command. 
 * */
class CommandProcessor {
private:
    CommandFactory m_commandFactory;

public:
    CommandProcessor(shared_ptr<IFilterService> filterService);
    string ProssessCommand(const string& request);
};

#endif // COMMAND_PROCESSOR_H