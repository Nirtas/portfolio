#pragma once
#define _CRT_SECURE_NO_WARNINGS

#include <string>
#include <vector>
#include <boost/multiprecision/cpp_int.hpp>

unsigned int mod(unsigned int, int);

long long mod(long long, long long);

unsigned int rotateLeft32Bit(unsigned int, int);

std::string appendZerosUntil(std::string, int, bool);

std::string toBinaryString(unsigned int);

unsigned int negative(unsigned int);

unsigned int reverseHexNumber(unsigned int);

std::vector<std::string> splitString(std::string, int);

std::vector<std::string> invertArray(std::vector<std::string>);

std::string to64BitBinaryString(unsigned int);

std::string join(std::vector<std::string>, std::string);

boost::multiprecision::cpp_int myPowm(const boost::multiprecision::cpp_int &, const boost::multiprecision::cpp_int &,
                                      const boost::multiprecision::cpp_int &);
