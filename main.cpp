#include "bloomFilter.h"
#include "hashFactory.h"
#include <fstream>
#include <iostream>
#include <sstream>

bool fileExistsAndNotEmpty(const std::string& path) {
    std::ifstream in(path);
    return in.peek() != std::ifstream::traits_type::eof();
}

int main() {
    std::string filePath = "data/bloom.txt";
    std::string configLine;
    bloomFilter filter(1, {});  // Dummy init

    if (fileExistsAndNotEmpty(filePath)) {
        std::ifstream in(filePath);
        std::getline(in, configLine);
        auto [size, funcs] = HashFactory::createFromConfigLine(configLine);
        filter = bloomFilter(size, funcs);

        std::string url;
        while (std::getline(in, url)) {
            if (!url.empty()) filter.add(url);
        }
        in.close();
    } else {
        std::getline(std::cin, configLine);
        auto [size, funcs] = HashFactory::createFromConfigLine(configLine);
        filter = bloomFilter(size, funcs);
        std::ofstream out(filePath);
        out << configLine << '\n';
        out.close();
    }

    std::string input;
    while (std::getline(std::cin, input)) {
        std::istringstream iss(input);
        int cmd;
        std::string url;

        if (!(iss >> cmd >> url)) continue;

        if (cmd == 1) {
            filter.add(url);
            std::ofstream out(filePath, std::ios::app);
            out << url << '\n';
        } else if (cmd == 2) {
            bool possibly = filter.possiblyContains(url);
            std::cout << (possibly ? "true " : "false ");
            if (possibly) {
                std::cout << (filter.isTrulyBlacklisted(url) ? "true" : "false");
            }
            std::cout << '\n';
        }
    }

    return 0;
}
