cmake_minimum_required(VERSION 3.22.1)

project("composetemplate")

macro(strip_quotes VAR)
    string(REPLACE "\"" "" ${VAR}_STR "${${VAR}}")
endmacro()

strip_quotes(API_KEY_DEBUG)
strip_quotes(API_KEY_RELEASE)
strip_quotes(BASE_URL_DEBUG)
strip_quotes(BASE_URL_RELEASE)
strip_quotes(XOR_MASK)
strip_quotes(EXPECTED_SIGNATURE_HASH)

add_library(
        native-lib
        SHARED
        native-lib.cpp)

target_compile_definitions(native-lib PRIVATE
        API_KEY_DEBUG="${API_KEY_DEBUG_STR}"
        API_KEY_RELEASE="${API_KEY_RELEASE_STR}"
        BASE_URL_DEBUG="${BASE_URL_DEBUG_STR}"
        BASE_URL_RELEASE="${BASE_URL_RELEASE_STR}"
        XOR_MASK="${XOR_MASK_STR}"
        EXPECTED_SIGNATURE_HASH="${EXPECTED_SIGNATURE_HASH_STR}")

find_library(
        log-lib
        log)

target_link_libraries(
        native-lib
        ${log-lib})
