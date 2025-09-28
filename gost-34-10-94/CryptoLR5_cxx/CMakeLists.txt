cmake_minimum_required(VERSION 3.28)
project(CryptoLR5_cxx)

set(CMAKE_CXX_STANDARD 17)

add_executable(CryptoLR5_cxx
        MD4.cpp
        Utils.cpp
        LinearCongruentialGenerator.cpp
        GeneratingKeys.cpp
        Main.cpp
)
