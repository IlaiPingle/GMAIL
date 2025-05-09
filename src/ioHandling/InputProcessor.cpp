#include "InputProcessor.h"

InputProcessor::InputProcessor(std::shared_ptr<IApplicationService> appService)
    : m_appService(appService) {
}

bool InputProcessor::processConfigLine(const std::string& line) {
    return m_appService->initialize(line);
}

std::string InputProcessor::processCommandLine(const std::string& line) {
    return m_appService->processCommand(line);
}