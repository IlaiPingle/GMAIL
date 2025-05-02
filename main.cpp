#include <iostream>
#include <string>
#include <memory>
#include "ioHandling/inputManager.h"

using namespace std;

int main() {
    string initialLine;
    unique_ptr <inputManager> manager = nullptr;

    while (getline(cin, initialLine)) {
        manager = inputManager::initFirstLine(initialLine);
        if (manager){
            break;
        }
    }
    if (!manager){
        return 1;
    }
    string line;
    while (getline(cin, line)) {
        string result = manager->convertLine(line);
        if (!result.empty()) {
            cout << result << endl;
        }
    }
    return 0;
}