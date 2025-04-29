#include "bloomFilter.h"
#include "hashFactory.h"
#include <fstream>
#include <iostream>
#include <sstream>

using namespace std;

bool fileExistsAndNotEmpty(const string& path) {
    ifstream in(path);
    if (!in.is_open()){
         return false; // File does not exist or cannot be opened
    }
    return in.peek() != ifstream::traits_type::eof();
}

int main() {
    string filePath = "data/bloom.txt";
    string configLine;
    bloomFilter filter(1, {});  // Dummy init

    if (fileExistsAndNotEmpty(filePath)) {
        ifstream in(filePath);
        getline(in, configLine);
        auto [size, funcs] = HashFactory::createFromConfigLine(configLine);
        filter = bloomFilter(size, funcs);

        string url;
        while (getline(in, url)) {
            if (!url.empty()) filter.add(url);
        }
        in.close();
    } else {
        getline(cin, configLine);
        auto [size, funcs] = HashFactory::createFromConfigLine(configLine);
        filter = bloomFilter(size, funcs);
        ofstream out(filePath);
        out << configLine << '\n';
        out.close();
    }

    string input;
    while (getline(cin, input)) {
        istringstream iss(input);
        int cmd;
        string url;

        if (!(iss >> cmd >> url)) continue;

        if (cmd == 1) {
            filter.add(url);
            ofstream out(filePath, ios::app);
            out << url << '\n';
        } else if (cmd == 2) {
            bool possibly = filter.contains(url);
            cout << (possibly ? "true " : "false ");
            if (possibly) {
                cout << (filter.checkFalsePositive(filter, url) ? "true" : "false");
            }
            cout << '\n';
        }
    }

    return 0;
}
