cmake_minimum_required(VERSION 3.14)
project(BloomFilter)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(${CMAKE_SOURCE_DIR})

# Add source files for the library
add_library(bloom_filter_lib
    # Original files
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/bloomFilter.cpp 
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/hashFactory.cpp
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/repeatedHash.cpp


    ${CMAKE_SOURCE_DIR}/src/commands/CommandFactory.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/AddCommand.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/CheckCommand.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/DeleteCommand.cpp

    ${CMAKE_SOURCE_DIR}/src/ioHandling/inputManager.cpp

    ${CMAKE_SOURCE_DIR}/src/networking/Server.cpp

    ${CMAKE_SOURCE_DIR}/src/services/FileStorageService.cpp
    ${CMAKE_SOURCE_DIR}/src/services/BloomFilterService.cpp
    ${CMAKE_SOURCE_DIR}/src/services/CommandProcessor.cpp
    ${CMAKE_SOURCE_DIR}/src/services/ApplicationService.cpp    
    ${CMAKE_SOURCE_DIR}/src/services/ApplicationConfig.cpp

    ${CMAKE_SOURCE_DIR}/src/utils/URLValidator.cpp
    ${CMAKE_SOURCE_DIR}/src/utils/DefaultURLNormalizer.cpp
)
# main application target
add_executable(bloom_filter_app ${CMAKE_SOURCE_DIR}/main.cpp)
target_link_libraries(bloom_filter_app bloom_filter_lib)

# # Find Google Test
# include(FetchContent)
# FetchContent_Declare(
#     googletest
#     URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
# )
# FetchContent_MakeAvailable(googletest)

# # Add test executable with all test files
# add_executable(runTests 
#     # Original test files
#     ${CMAKE_SOURCE_DIR}/Tests/test_inputManager.cpp
    
#     # test server
#     ${CMAKE_SOURCE_DIR}/Tests/test_server.cpp

#     # test main
#     ${CMAKE_SOURCE_DIR}/Tests/test_main.cpp
    
#     # test fileManager
#     ${CMAKE_SOURCE_DIR}/Tests/test_fileManager.cpp
# )
# target_link_libraries(runTests gtest_main bloom_filter)

# # Google Test integration
# include(GoogleTest)
# gtest_discover_tests(runTests)

# Create directory for test data
file(MAKE_DIRECTORY ${CMAKE_BINARY_DIR}/data)