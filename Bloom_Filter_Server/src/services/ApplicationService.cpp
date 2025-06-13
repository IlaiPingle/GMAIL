#include "ApplicationService.h"


ApplicationService::ApplicationService(
    shared_ptr<CommandProcessor> commandProcessor) :
    m_commandProcessor(commandProcessor){}
    
    string ApplicationService::processCommand(const string& commandLine) {
        return m_commandProcessor->ProssessCommand(commandLine);
    }
    
    