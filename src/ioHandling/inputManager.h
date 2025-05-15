# ifndef INPUTMANAGER_H
# define INPUTMANAGER_H
#include "../utils/URLValidator.h"
#include <string>
#include <memory>
#include <sstream>
using namespace std; 
class InputManager {
    private:
        
    public:
        InputManager();
        ~InputManager() = default; // Destructor 
        static bool splitRequest(string& command, string& url);
        

};
#endif // INPUTMANAGER_H