#include "bloomFilter.h"
#include "hashFactory.h"
#include <fstream> 
#include <iostream>
#include <sstream>

using namespace std;

static const string BloomFilterPath = "data/bloom.txt";
static const string bitArrayPath = "data/bitArray.txt";


bool fileExistsAndNotEmpty() {
    std::ifstream in(BloomFilterPath);
    if( !(in.peek() != ifstream::traits_type::eof())) {
        return false;
    }
    in.close();
    std ::ifstream in(bitArrayPath);
    return in.peek() != ifstream::traits_type::eof();
}
int runProgram(){
    if (!fileExistsAndNotEmpty()) {
        string initalLine;
        cin >> initalLine ;
        string sizeWord = initalLine.substr(0, initalLine.find(" "));
        size_t bitArreySize = stoi(sizeWord);
        string hashInfoStr = initalLine.substr(initalLine.find(" ") + 1);
        bloomFilter filter(bitArreySize , HashFactory :: createHashFunctions(hashInfoStr)); 
    );

}