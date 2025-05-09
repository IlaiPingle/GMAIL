# ifndef INPUTMANAGER_H
# define INPUTMANAGER_H

#include <string>
#include <memory>
#include "../services/CommandProcessor.h"
using namespace std; 
class InputManager {
    private:
        unique_ptr<CommandProcessor> m_commandProcessor; // Command processor for handling commands
        
    public:
        InputManager();
        InputManager(unique_ptr<CommandProcessor> commandProcessor);
        ~InputManager() = default; // Destructor 
        string processCommand(const string& line);
        static unique_ptr<InputManager> createFromConfig(const string& configLine);
};
#endif // INPUTMANAGER_H