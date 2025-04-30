cmake_minimum_required(VERSION 3.14)
project(BloomFilter)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(${CMAKE_SOURCE_DIR})

# Add source files for the library
add_library(bloom_filter
    bloomFilter.cpp
    repeatedHash.cpp
)

# Find Google Test
include(FetchContent)
FetchContent_Declare(
    googletest
    URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

# Add test executable
add_executable(runTests bloom_filter_test.cpp)
target_link_libraries(runTests gtest_main bloom_filter)

# Add Google Test
include(GoogleTest)
gtest_discover_tests(runTests)

# Create directory for test data
file(MAKE_DIRECTORY ${CMAKE_BINARY_DIR}/data)
