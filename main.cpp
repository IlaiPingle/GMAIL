#include <iostream>
#include <string>
#include <memory>
#include "ioHandling/inputManager.h"

using namespace std;

bool fileExistsAndNotEmpty(const string& path) {
    ifstream in(path);
    return in.peek() != ifstream::traits_type::eof();
}

int main() {
    string initialLine;
    unique_ptr<InputManager> manager = nullptr;

    while (getline(cin, initialLine)) {
        manager = InputManager::initFirstLine(initialLine);
        if (manager){
            break;
        }
    }
    if (!manager){
        cout << "Error: Invalid input format." << endl;
        return 1;
    }
    string line;
    while (getline(std::cin, line)) {
        string result = manager->convertLine(line);
        if (!result.empty()) {
            cout << result << endl;
        }
    }
    return 0;
}