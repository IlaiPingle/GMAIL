#ifndef INPUTPROCESSOR_H
#define INPUTPROCESSOR_H

#include <memory>
#include <string>
#include "../interfaces/IApplicationService.h"

class InputProcessor {
private:
    std::shared_ptr<IApplicationService> m_appService;

public:
    InputProcessor(std::shared_ptr<IApplicationService> appService);
    bool processConfigLine(const std::string& line);
    std::string processCommandLine(const std::string& line);
};
#endif // INPUTPROCESSOR_H