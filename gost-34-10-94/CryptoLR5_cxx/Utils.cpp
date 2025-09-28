#include "Utils.h"
#include <sstream>
#include <cmath>
#include <iterator>

unsigned int mod(unsigned int number, int module) {
	unsigned int result = number;
	if (module < 1) result += module;
	while (result < 0) result += module;
	while (result >= module) result -= module;
	return result;
}

long long mod(long long number, long long module) {
	long long result = number;
	if (module < 1) result += module;
	while (result < 0) result += module;
	while (result >= module) result -= module;
	return result;
}

unsigned int rotateLeft32Bit(unsigned int number, int shift) {
	if (number == 0) return 0;
	if (mod(shift, 32) == 0) return number;
	return ((number << shift) | (number >> (32 - shift)));
}

std::string appendZerosUntil(std::string string, int length, bool atEnd) {
	std::string result = string;
	while (result.length() < length) {
		if (atEnd) {
			result += '0';
		}
		else {
			result = result.insert(0, "0");
		}
	}
	return result;
}

std::string toBinaryString(unsigned int number) {
	char* binaryString = new char[100];
	_itoa(number, binaryString, 2);
	return binaryString;
}

unsigned int negative(unsigned int number) {
	std::string binaryString = appendZerosUntil(toBinaryString(number), 32, false);
	for (int i = 0; i < binaryString.length(); i++) {
		binaryString[i] = (binaryString[i] == '0') ? '1' : '0';
	}
	return std::stoul(binaryString, 0, 2);
}

unsigned int reverseHexNumber(unsigned int number) {
	return ((number & 0xFF) << 24) | (((number >> 8) & 0xFF) << 16) | (((number >> 16) & 0xFF) << 8) | ((number >> 24) & 0xFF);
}

std::vector<std::string> splitString(std::string string, int substringLength) {
	std::vector<std::string> substrings((int) ceil((double) string.length() / substringLength));
	for (int i = 0; i < substrings.size(); i++) {
		if (i == substrings.size() - 1) {
			substrings[i] = string.substr(i * substringLength, string.size() - i * substringLength);
			break;
		}
		substrings[i] = string.substr(i * substringLength, substringLength);
	}
	return substrings;
}

std::vector<std::string> invertArray(std::vector<std::string> array) {
	std::vector<std::string> invertedArray(array);
	int j = array.size() - 1;
	for (int i = 0; i < array.size(); i++) {
		invertedArray[i] = array[j--];
	}
	return invertedArray;
}

std::string to64BitBinaryString(unsigned int number) {
	unsigned int num = reverseHexNumber(number);
	std::string binaryString = toBinaryString(num);
	binaryString = appendZerosUntil(binaryString, 64, false);
	std::vector<std::string> substrings = splitString(binaryString, 32);
	substrings = invertArray(substrings);
	return join(substrings, "");
}

std::string join(std::vector<std::string> vector, std::string delimiter) {
	std::ostringstream result;
	std::copy(vector.begin(), vector.end(), std::ostream_iterator<std::string>(result, delimiter.c_str()));
	return result.str();
}

boost::multiprecision::cpp_int myPowm(const boost::multiprecision::cpp_int &a, const boost::multiprecision::cpp_int &p,
									  const boost::multiprecision::cpp_int &c) {
	using std::abs;
	boost::multiprecision::cpp_int r = powm(a, p, c);
	return r<0? r + abs(c) : r;
}
