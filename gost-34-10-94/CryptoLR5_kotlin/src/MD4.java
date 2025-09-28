public class MD4 {
    static long F(long A, long B, long C, long D, long X, int s) {
        long t = (A + ((B & C) | (not(B) & D)) + X) & 0xFFFFFFFFL;
        return Utils.INSTANCE.rotateLeft32bit(t, s);
    }

    static long G(long A, long B, long C, long D, long X, int s) {
        long t = (A + ((B & C) | (B & D) | (C & D)) + X + 0x5A827999) & 0xFFFFFFFFL;
        return Utils.INSTANCE.rotateLeft32bit(t, s);
    }

    static long H(long A, long B, long C, long D, long X, int s) {
        long t = (A + (B ^ C ^ D) + X + 0x6ED9EBA1) & 0xFFFFFFFFL;
        return Utils.INSTANCE.rotateLeft32bit(t, s);
    }

    static long not(long number) {
        StringBuilder binaryString = new StringBuilder(appendZerosUntil(Long.toBinaryString(number), 32, false));
        for (int i = 0; i < binaryString.length(); i++) {
            binaryString.setCharAt(i, (binaryString.charAt(i) == '0') ? '1' : '0');
        }
        return Long.parseLong(binaryString.toString(), 2);
    }

    static String appendZerosUntil(String string, int length, boolean atEnd) {
        StringBuilder result = new StringBuilder(string);
        while (result.length() < length) {
            if (atEnd) {
                result.append('0');
            } else {
                result.insert(0, '0');
            }
        }
        return result.toString();
    }

    static String to64BitBinaryString(long number) {
        long num = reverseHexNumber(number);
        String binaryString = Long.toBinaryString(num);
        binaryString = appendZerosUntil(binaryString, 64, false);
        String[] substrings = splitString(binaryString, 32);
        substrings = invertArray(substrings);
        return String.join("", substrings);
    }

    static String[] splitString(String string, int substringLength) {
        String[] substrings = new String[(int) Math.ceil((double) string.length() / substringLength)];
        for (int i = 0; i < substrings.length; i++) {
            if (i == substrings.length - 1) {
                substrings[i] = string.substring(i * substringLength);
                break;
            }
            substrings[i] = string.substring(i * substringLength, i * substringLength + substringLength);
        }
        return substrings;
    }

    static <T> T[] invertArray(T[] array) {
        T[] invertedArray = array.clone();
        int j = array.length - 1;
        for (int i = 0; i < array.length; i++) {
            invertedArray[i] = array[j--];
        }
        return invertedArray;
    }

    static long reverseHexNumber(long number) {
        return ((number & 0xFF) << 24) | (((number >> 8) & 0xFF) << 16) | (((number >> 16) & 0xFF) << 8) |
               ((number >> 24) & 0xFF);
    }

    static long[] hash(String message) {
        long[] h = new long[] {0x67452301L, 0xEFCDAB89L, 0x98BADCFEL, 0x10325476L};
        String[] messageSubstrings = new String[message.length()];
        for (int i = 0; i < messageSubstrings.length; i++) {
            messageSubstrings[i] = appendZerosUntil(Integer.toBinaryString(message.charAt(i)), 8, false);
        }
        String m = String.join("", messageSubstrings);
        int b = m.length();
        m += '1';
        String bIn64BitString = to64BitBinaryString(b);
        b++;
        int n = b - (b / 512) * 512;
        if (n > 448) {
            m = appendZerosUntil(m, 512 + 448 - n, true);
        } else {
            m = appendZerosUntil(m, 448, true);
        }
        m += bIn64BitString;

        String[] blocks = splitString(m, 512);

        long[] H = new long[] {h[0], h[1], h[2], h[3]};

        for (String block : blocks) {
            long[] X = new long[16];
            String[] XSubstrings = splitString(block, 32);
            for (int j = 0; j < XSubstrings.length; j++) {
                X[j] = reverseHexNumber(Long.parseLong(XSubstrings[j], 2));
            }

            long A = H[0];
            long B = H[1];
            long C = H[2];
            long D = H[3];

            A = F(A, B, C, D, X[0], 3) & 0xFFFFFFFFL;
            D = F(D, A, B, C, X[1], 7) & 0xFFFFFFFFL;
            C = F(C, D, A, B, X[2], 11) & 0xFFFFFFFFL;
            B = F(B, C, D, A, X[3], 19) & 0xFFFFFFFFL;
            A = F(A, B, C, D, X[4], 3) & 0xFFFFFFFFL;
            D = F(D, A, B, C, X[5], 7) & 0xFFFFFFFFL;
            C = F(C, D, A, B, X[6], 11) & 0xFFFFFFFFL;
            B = F(B, C, D, A, X[7], 19) & 0xFFFFFFFFL;
            A = F(A, B, C, D, X[8], 3) & 0xFFFFFFFFL;
            D = F(D, A, B, C, X[9], 7) & 0xFFFFFFFFL;
            C = F(C, D, A, B, X[10], 11) & 0xFFFFFFFFL;
            B = F(B, C, D, A, X[11], 19) & 0xFFFFFFFFL;
            A = F(A, B, C, D, X[12], 3) & 0xFFFFFFFFL;
            D = F(D, A, B, C, X[13], 7) & 0xFFFFFFFFL;
            C = F(C, D, A, B, X[14], 11) & 0xFFFFFFFFL;
            B = F(B, C, D, A, X[15], 19) & 0xFFFFFFFFL;

            A = G(A, B, C, D, X[0], 3) & 0xFFFFFFFFL;
            D = G(D, A, B, C, X[4], 5) & 0xFFFFFFFFL;
            C = G(C, D, A, B, X[8], 9) & 0xFFFFFFFFL;
            B = G(B, C, D, A, X[12], 13) & 0xFFFFFFFFL;
            A = G(A, B, C, D, X[1], 3) & 0xFFFFFFFFL;
            D = G(D, A, B, C, X[5], 5) & 0xFFFFFFFFL;
            C = G(C, D, A, B, X[9], 9) & 0xFFFFFFFFL;
            B = G(B, C, D, A, X[13], 13) & 0xFFFFFFFFL;
            A = G(A, B, C, D, X[2], 3) & 0xFFFFFFFFL;
            D = G(D, A, B, C, X[6], 5) & 0xFFFFFFFFL;
            C = G(C, D, A, B, X[10], 9) & 0xFFFFFFFFL;
            B = G(B, C, D, A, X[14], 13) & 0xFFFFFFFFL;
            A = G(A, B, C, D, X[3], 3) & 0xFFFFFFFFL;
            D = G(D, A, B, C, X[7], 5) & 0xFFFFFFFFL;
            C = G(C, D, A, B, X[11], 9) & 0xFFFFFFFFL;
            B = G(B, C, D, A, X[15], 13) & 0xFFFFFFFFL;

            A = H(A, B, C, D, X[0], 3) & 0xFFFFFFFFL;
            D = H(D, A, B, C, X[8], 9) & 0xFFFFFFFFL;
            C = H(C, D, A, B, X[4], 11) & 0xFFFFFFFFL;
            B = H(B, C, D, A, X[12], 15) & 0xFFFFFFFFL;
            A = H(A, B, C, D, X[2], 3) & 0xFFFFFFFFL;
            D = H(D, A, B, C, X[10], 9) & 0xFFFFFFFFL;
            C = H(C, D, A, B, X[6], 11) & 0xFFFFFFFFL;
            B = H(B, C, D, A, X[14], 15) & 0xFFFFFFFFL;
            A = H(A, B, C, D, X[1], 3) & 0xFFFFFFFFL;
            D = H(D, A, B, C, X[9], 9) & 0xFFFFFFFFL;
            C = H(C, D, A, B, X[5], 11) & 0xFFFFFFFFL;
            B = H(B, C, D, A, X[13], 15) & 0xFFFFFFFFL;
            A = H(A, B, C, D, X[3], 3) & 0xFFFFFFFFL;
            D = H(D, A, B, C, X[11], 9) & 0xFFFFFFFFL;
            C = H(C, D, A, B, X[7], 11) & 0xFFFFFFFFL;
            B = H(B, C, D, A, X[15], 15) & 0xFFFFFFFFL;

            H[0] = (H[0] + A) & 0xFFFFFFFFL;
            H[1] = (H[1] + B) & 0xFFFFFFFFL;
            H[2] = (H[2] + C) & 0xFFFFFFFFL;
            H[3] = (H[3] + D) & 0xFFFFFFFFL;
        }

        H[0] = reverseHexNumber(H[0]);
        H[1] = reverseHexNumber(H[1]);
        H[2] = reverseHexNumber(H[2]);
        H[3] = reverseHexNumber(H[3]);

        return H;
    }
}

