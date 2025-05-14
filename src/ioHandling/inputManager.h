# ifndef INPUTMANAGER_H
# define INPUTMANAGER_H

#include <string>
#include <memory>
#include "../utils/URLValidator.h"
using namespace std; 
class InputManager {
    private:
        
    public:
        InputManager();
        ~InputManager() = default; // Destructor 
        static bool InputManager::splitRequest(string& command, string& url);
        

};
#endif // INPUTMANAGER_H