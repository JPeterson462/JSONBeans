package ftljson;

/**
 * @author Ioannis Tsakpinis
 * @author Kai Burjack
 */
public class ParserC {

    private ParserC() {
    }

    private static boolean ignore(char b) {
        if (b == ' ') {
            return true;
        }
        switch (b) {
        case '\t': // 9
        case '\n': // 10
        case '\r': // 13
        case ',': // 44
        case ':': // 58
            return true;
        }
        return false;
    }

    private static int skipIgnored(char[] input, int pos) {
        while (ignore(input[++pos])) {
            ;
        }
        return pos;
    }

    private static int append(char[] src, int srcPos, char[] dest, int destPos, int srcTo) {
        int length = srcTo - srcPos;
        System.arraycopy(src, srcPos, dest, destPos, length);
        return destPos + length;
    }

    private static int unescape(char[] input, int start, int i, int end, char[] res) {
        int len = 0;
        for (; i < end; i++) {
            if (input[i] != '\\') {
                continue;
            }
            if (start < i) {
                len = append(input, start, res, len, i);
            }
            char c = input[++i];
            switch (c) {
            case 'n':
                c = '\n';
                break;
            case 'r':
                c = '\r';
                break;
            }
            res[len++] = c;
            start = i + 1;
        }
        if (start < i) {
            len = append(input, start, res, len, i);
        }

        return len;
    }

    private static int escapedstring(char[] input, ParseListener listener, boolean inobject, int start, int pos) {
        int firstescaped = pos;
        while (true) {
            char c = input[pos];
            if (c == '\\') {
                pos++;
            } else if (c == '"') {
                char[] res = new char[pos - start];
                int len = unescape(input, start, firstescaped, pos, res);
                if (inobject) {
                    listener.beginObjectEntry(new String(res, 0, len));
                    pos = json(input, listener, false, false, pos);
                } else {
                    listener.stringLiteral(new String(res, 0, len));
                }
                return pos;
            }
            pos++;
        }
    }

    private static int string(char[] input, ParseListener listener, boolean inobject, int pos) {
        int start = pos;
        while (true) {
            char b = input[pos];
            if (b == '"') {
                break;
            }
            if (b == '\\') {
                return escapedstring(input, listener, inobject, start, pos);
            }
            pos++;
        }
        if (inobject) {
            listener.beginObjectEntry(new String(input, start, pos - start));
            pos = json(input, listener, false, false, pos);
        } else {
            listener.stringLiteral(new String(input, start, pos - start));
        }
        return pos;
    }

    private static int exponent(char[] p, ParseListener listener, double v, int pi, boolean neg) {
        boolean eneg = false;
        char c = p[++pi];
        if (c == '-') {
            eneg = true;
            pi++;
        } else if (c == '+') {
            pi++;
        }
        int e = 0;
        while (true) {
            c = p[pi];
            if (c < '0' || '9' < c) {
                break;
            }
            e = 10 * e + c - '0';
            pi++;
        }
        double se = 1.0;
        for (; e > 0; se *= 10.0, e--) {
            ;
        }
        if (eneg) {
            v /= se;
        } else {
            v *= se;
        }
        listener.doubleLiteral(neg ? -v : v);
        return pi;
    }

    private static int decimalAndExponent(char[] p, ParseListener listener, long v, int pi, boolean neg) {
        char c = p[pi];
        if (c == '.') {
            double vd = (double) v;
            long f = 0;
            long scale = 1;
            while (true) {
                c = p[++pi];
                if (c < '0' || '9' < c) {
                    break;
                }
                f = f * 10 + c - '0';
                scale *= 10;
            }
            vd += (double) f / scale;
            if (c == 'e' || c == 'E') {
                return exponent(p, listener, vd, pi, neg);
            }
            listener.doubleLiteral(neg ? -vd : vd);
        } else if (c == 'e' || c == 'E') {
            return exponent(p, listener, (double) v, pi, neg);
        } else {
            listener.longLiteral(neg ? -v : v);
        }
        return pi;
    }

    private static int number(char[] p, ParseListener listener, int pi, boolean neg) {
        if (neg) {
            pi++;
        }
        long v = 0;
        while (true) {
            char c = p[pi];
            if (c < '0' || '9' < c) {
                break;
            }
            v = 10 * v + c - '0';
            pi++;
        }
        return decimalAndExponent(p, listener, v, pi, neg);
    }

    private static int json(char[] input, ParseListener listener, boolean inobject, boolean cntn, int pos) {
        do {
            pos = skipIgnored(input, pos);
            char c = input[pos];
            if (c == '"') { // 22
                pos = string(input, listener, inobject, pos + 1);
                continue;
            }
            boolean neg = false;
            switch (c - '-') {
            case 0: // '-' 45
                neg = true;
            case 3: // '0' 48
            case 4: // '1' 49
            case 5: // '2' 50
            case 6: // '3' 51
            case 7: // '4' 52
            case 8: // '5' 53
            case 9: // '6' 54
            case 10: // '7' 55
            case 11: // '8' 56
            case 12: // '9' 57
                pos = number(input, listener, pos, neg) - 1;
                continue;
            }
            if (pos == (pos = rest(input, listener, pos, c))) {
                break;
            }
        } while (cntn);
        return pos;
    }

    private static int rest(char[] input, ParseListener listener, int pos, char c) {
        switch (c - '[') {
        case 0: // '[' 91
            listener.beginList();
            pos = json(input, listener, false, true, pos);
            break;
        case 2: // ']' 93
            listener.endList();
            break;
        case 11: // 'f' 102
            listener.booleanLiteral(false);
            pos += 4;
            break;
        case 19: // 'n' 110
            listener.nullLiteral();
            pos += 3;
            break;
        case 25: // 't' 116
            listener.booleanLiteral(true);
            pos += 3;
            break;
        case 32: // '{' 123
            listener.beginObject();
            pos = json(input, listener, true, true, pos);
            break;
        case 34: // '}' 125
            listener.endObject();
            break;
        }
        return pos;
    }

    public static int json(char[] input, ParseListener listener) {
        return json(input, listener, false, false, -1);
    }
}
