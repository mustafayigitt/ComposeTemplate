cmake_minimum_required(VERSION 3.22.1)

project("composetemplate")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN ON)

add_library(
        native-lib
        SHARED
        native-lib.cpp)

target_include_directories(native-lib PRIVATE ${GENERATED_SECRETS_PATH})

target_compile_definitions(native-lib PRIVATE
        CM_PART="${CM_PART}"
        K_PART="${K_PART}"
        NATIVE_RUNTIME_CHECKS_ENABLED=${NATIVE_RUNTIME_CHECKS_ENABLED})

target_compile_options(native-lib PRIVATE
        -fvisibility=hidden
        -fvisibility-inlines-hidden
        -ffunction-sections
        -fdata-sections)

# 16KB Page Support
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=16384 -Wl,--gc-sections -Wl,-z,relro -Wl,-z,now")
set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} -Wl,-s")

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${log-lib})
