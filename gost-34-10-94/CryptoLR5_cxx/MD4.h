#pragma once
#include <string>
#include <vector>

class MD4 {
public:
	MD4();
	std::vector<unsigned int> calculateHash(std::string);
private:
	std::vector<unsigned int> h;
	void init();
	unsigned int F(unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, int);
	unsigned int G(unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, int);
	unsigned int H(unsigned int, unsigned int, unsigned int, unsigned int, unsigned int, int);
};