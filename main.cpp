#include <iostream>
#include "bloomFilter.h"
using namespace std;
int main(){
   while (true)
   {
   int arraySize;
    int numHashes;
    cin >> arraySize >> numHashes; // Read the size of the bloom filter and the number of hash functions
    bloomFilter bf(arraySize, numHashes); // Create a bloom filter with the specified size and number of hash functions
    string url;
    int choice;
    cin >> choice >> url; // Read the user's choice and the URL
    switch (choice) {
        case 1: // Add URL
            URL1(bf, url); // Add the URL to the bloom filter
        case 2: // Check URL
            if (bf.contains(url)) { // Check if the URL is in the bloom filter
                cout << "URL is possibly in the bloom filter." << endl;
            } else {
                cout << "URL is definitely not in the bloom filter." << endl;
            }
            break;
        default: // Invalid choice
            cout << "Invalid choice. Please enter 1 or 2." << endl;
            break;
    }
   }
   
}
void URL1(bloomFilter& bf, string url) {
    bf.add(url); // Add the URL to the bloom filter
}