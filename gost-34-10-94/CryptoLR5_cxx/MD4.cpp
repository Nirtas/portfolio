#include "Utils.h"
#include "MD4.h"

unsigned int MD4::F(unsigned int A, unsigned int B, unsigned int C, unsigned int D, unsigned int X, int s) {
	unsigned int t = (A + ((B & C) | (negative(B) & D)) + X);
	return rotateLeft32Bit(t, s);
}

unsigned int MD4::G(unsigned int A, unsigned int B, unsigned int C, unsigned int D, unsigned int X, int s) {
	unsigned int t = (A + ((B & C) | (B & D) | (C & D)) + X + 0x5A827999);
	return rotateLeft32Bit(t, s);
}

unsigned int MD4::H(unsigned int A, unsigned int B, unsigned int C, unsigned int D, unsigned int X, int s) {
	unsigned int t = (A + (B ^ C ^ D) + X + 0x6ED9EBA1);
	return rotateLeft32Bit(t, s);
}

std::vector<unsigned int> MD4::calculateHash(std::string text) {
	std::vector<std::string> textSubstrings(text.size());
	for (int i = 0; i < textSubstrings.size(); i++) {
		textSubstrings[i] = appendZerosUntil(toBinaryString(text[i]), 8, false);
	}
	std::string m = join(textSubstrings, "");
	int b = m.length();
	m += '1';
	std::string bIn64BitString = to64BitBinaryString(b);
	b++;
	int n = b - (b / 512) * 512;
	if (n > 448) {
		m = appendZerosUntil(m, 512 + 448 - n, true);
	}
	else {
		m = appendZerosUntil(m, 448, true);
	}
	m += bIn64BitString;
	std::vector<std::string> blocks = splitString(m, 512);
	std::vector<unsigned int> Hvector = {h[0], h[1], h[2], h[3]};
	for (int i = 0; i < blocks.size(); i++) {
		std::vector<unsigned int> X(16);
		std::vector<std::string> XSubstrings = splitString(blocks[i], 32);
		for (int j = 0; j < XSubstrings.size(); j++) {
			X[j] = reverseHexNumber(std::stoul(XSubstrings[j], 0, 2));
		}
		unsigned int A = Hvector[0];
		unsigned int B = Hvector[1];
		unsigned int C = Hvector[2];
		unsigned int D = Hvector[3];

		A = F(A, B, C, D, X[0], 3);
		D = F(D, A, B, C, X[1], 7);
		C = F(C, D, A, B, X[2], 11);
		B = F(B, C, D, A, X[3], 19);
		A = F(A, B, C, D, X[4], 3);
		D = F(D, A, B, C, X[5], 7);
		C = F(C, D, A, B, X[6], 11);
		B = F(B, C, D, A, X[7], 19);
		A = F(A, B, C, D, X[8], 3);
		D = F(D, A, B, C, X[9], 7);
		C = F(C, D, A, B, X[10], 11);
		B = F(B, C, D, A, X[11], 19);
		A = F(A, B, C, D, X[12], 3);
		D = F(D, A, B, C, X[13], 7);
		C = F(C, D, A, B, X[14], 11);
		B = F(B, C, D, A, X[15], 19);

		A = G(A, B, C, D, X[0], 3);
		D = G(D, A, B, C, X[4], 5);
		C = G(C, D, A, B, X[8], 9);
		B = G(B, C, D, A, X[12], 13);
		A = G(A, B, C, D, X[1], 3);
		D = G(D, A, B, C, X[5], 5);
		C = G(C, D, A, B, X[9], 9);
		B = G(B, C, D, A, X[13], 13);
		A = G(A, B, C, D, X[2], 3);
		D = G(D, A, B, C, X[6], 5);
		C = G(C, D, A, B, X[10], 9);
		B = G(B, C, D, A, X[14], 13);
		A = G(A, B, C, D, X[3], 3);
		D = G(D, A, B, C, X[7], 5);
		C = G(C, D, A, B, X[11], 9);
		B = G(B, C, D, A, X[15], 13);

		A = H(A, B, C, D, X[0], 3);
		D = H(D, A, B, C, X[8], 9);
		C = H(C, D, A, B, X[4], 11);
		B = H(B, C, D, A, X[12], 15);
		A = H(A, B, C, D, X[2], 3);
		D = H(D, A, B, C, X[10], 9);
		C = H(C, D, A, B, X[6], 11);
		B = H(B, C, D, A, X[14], 15);
		A = H(A, B, C, D, X[1], 3);
		D = H(D, A, B, C, X[9], 9);
		C = H(C, D, A, B, X[5], 11);
		B = H(B, C, D, A, X[13], 15);
		A = H(A, B, C, D, X[3], 3);
		D = H(D, A, B, C, X[11], 9);
		C = H(C, D, A, B, X[7], 11);
		B = H(B, C, D, A, X[15], 15);

		Hvector[0] += A;
		Hvector[1] += B;
		Hvector[2] += C;
		Hvector[3] += D;
	}

	Hvector[0] = reverseHexNumber(Hvector[0]);
	Hvector[1] = reverseHexNumber(Hvector[1]);
	Hvector[2] = reverseHexNumber(Hvector[2]);
	Hvector[3] = reverseHexNumber(Hvector[3]);

	return Hvector;
}

MD4::MD4() {
	init();
}

void MD4::init() {
	h.assign(4, 0);
	h[0] = 0x67452301;
	h[1] = 0xEFCDAB89;
	h[2] = 0x98BADCFE;
	h[3] = 0x10325476;
}