cmake_minimum_required(VERSION 3.14)
project(BloomFilter)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(${CMAKE_SOURCE_DIR})

# Add source files for the library
add_library(bloom_filter
    # Original files
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/bloomFilter.cpp
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/repeatedHash.cpp
    ${CMAKE_SOURCE_DIR}/src/bloom_Filter/hashFactory.cpp
    ${CMAKE_SOURCE_DIR}/src/ioHandling/inputManager.cpp
    ${CMAKE_SOURCE_DIR}/src/services/CommandProcessor.cpp
    ${CMAKE_SOURCE_DIR}/src/utils/URLValidator.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/CommandFactory.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/AddCommand.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/CheckCommand.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/DeleteCommand.cpp
    ${CMAKE_SOURCE_DIR}/src/services/BloomFilterService.cpp
    ${CMAKE_SOURCE_DIR}/src/services/FileStorageService.cpp
    ${CMAKE_SOURCE_DIR}/src/ioHandling/InputProcessor.cpp
    ${CMAKE_SOURCE_DIR}/src/services/ApplicationService.cpp
    ${CMAKE_SOURCE_DIR}/src/networking/Server.cpp

    # New SOLID implementation files
    ${CMAKE_SOURCE_DIR}/src/utils/DefaultURLFormatter.cpp
    ${CMAKE_SOURCE_DIR}/src/utils/DefaultURLNormalizer.cpp
    ${CMAKE_SOURCE_DIR}/src/utils/DefaultBitVector.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/AddCommandCreator.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/CheckCommandCreator.cpp
    ${CMAKE_SOURCE_DIR}/src/commands/DeleteCommandCreator.cpp
    ${CMAKE_SOURCE_DIR}/src/services/ApplicationConfig.cpp
    ${CMAKE_SOURCE_DIR}/src/networking/ConnectionHandler.cpp
    ${CMAKE_SOURCE_DIR}/src/networking/TCPSocketConnection.cpp
    ${CMAKE_SOURCE_DIR}/src/networking/TCPSocketListener.cpp
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

# Add test executable with all test files
add_executable(runTests 
    # Original test files
    #${CMAKE_SOURCE_DIR}/Tests/test_inputManager.cpp
    
    # New test files
    ${CMAKE_SOURCE_DIR}/Tests/test_server.cpp
)
target_link_libraries(runTests gtest_main bloom_filter)

# Add executables
add_executable(bloom_filter_app ${CMAKE_SOURCE_DIR}/main.cpp)
target_link_libraries(bloom_filter_app bloom_filter)

# Google Test integration
include(GoogleTest)
gtest_discover_tests(runTests)

# Create directory for test data
file(MAKE_DIRECTORY ${CMAKE_BINARY_DIR}/data)