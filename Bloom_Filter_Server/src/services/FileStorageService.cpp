#include "FileStorageService.h"

bool FileStorageService::saveBlacklist(const unordered_set<string>& blacklist) {
    ofstream file(m_blacklistFile);
    if (!file) {
        return false;
    }
    for (const auto& url : blacklist) {
        file << url << endl;
    }
    return true;
}

bool FileStorageService::addToBlacklist(const string& url) {
    ofstream file(m_blacklistFile, ios::app); // append to the end of the file.
    if (!file) {
        return false;
    }
    file << url << endl;
    return true;
}

bool FileStorageService::loadBlacklist(unordered_set<string>& blacklist) {
    ifstream file(m_blacklistFile);
    if (!file) {
        return false;
    }
    string url;
    while (getline(file, url)) {
        blacklist.insert(url);
    }
    return true;
}

bool FileStorageService::loadBitArray(const shared_ptr<bloomFilter>& bloomFilter) {
    ifstream file(m_blacklistFile);
    if (!file) {
        return false;
    }
    string line;
    while (getline(file, line)) {
        bloomFilter->add(line);
    }
    return true;
}

bool FileStorageService::removeFromBlacklist(const string& url) {
    // Load current blacklist
    unordered_set<string> blacklist;
    loadBlacklist(blacklist);
    // Check if URL exists in blacklist
    auto exixstURL = blacklist.find(url);
    if (exixstURL == blacklist.end()) {
        return false; // URL not found
    }
    // Remove URL from blacklist
    blacklist.erase(exixstURL);
    saveBlacklist(blacklist);
    return true;
}

bool FileStorageService::isInBlacklist(const string& url) {
    ifstream file(m_blacklistFile);
    string line;
    if (!file) {
        return false;
    }
    while (getline(file, line)) {
        if (line == url) {
            return true;
        }
    }
    return false;
}
