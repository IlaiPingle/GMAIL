cmake_minimum_required(VERSION 3.14)
project(BloomFilter)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(${CMAKE_SOURCE_DIR})

# Add source files for the library
add_library(bloom_filter
    ${CMAKE_SOURCE_DIR}/bloom_Filter/bloomFilter.cpp
    ${CMAKE_SOURCE_DIR}/bloom_Filter/repeatedHash.cpp
    ${CMAKE_SOURCE_DIR}/bloom_Filter/hashFactory.cpp
    ${CMAKE_SOURCE_DIR}/ioHandling/inputManager.cpp
    ${CMAKE_SOURCE_DIR}/ioHandling/fileManager.cpp
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
add_executable(runTests 
${CMAKE_SOURCE_DIR}/Tests/test_fileManager.cpp
${CMAKE_SOURCE_DIR}/Tests/test_inputManager.cpp
${CMAKE_SOURCE_DIR}/Tests/test_main.cpp)
target_link_libraries(runTests gtest_main bloom_filter)

add_executable(bloom_filter_app ${CMAKE_SOURCE_DIR}/main.cpp)
target_link_libraries(bloom_filter_app bloom_filter)

# Add Google Test
include(GoogleTest)
gtest_discover_tests(runTests)

# Create directory for test data
file(MAKE_DIRECTORY ${CMAKE_BINARY_DIR}/data)
