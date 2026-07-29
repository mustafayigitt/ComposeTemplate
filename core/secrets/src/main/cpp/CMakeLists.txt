cmake_minimum_required(VERSION 3.22.1)

project("composetemplate")

add_library(
        native-lib
        SHARED
        native-lib.cpp)

target_include_directories(native-lib PRIVATE ${GENERATED_SECRETS_PATH})

target_compile_definitions(native-lib PRIVATE
        CM_PART="${CM_PART}"
        K_PART="${K_PART}")

# 16KB Page Support
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384")

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${log-lib})
