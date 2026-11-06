package robotsim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small JSON reader and writer, just enough for robot design files. Objects become Maps,
 * arrays become Lists, numbers become Doubles. (The simulator core avoids extra libraries.)
 */
final class Json {

    private Json() {
    }

    /** A problem in a JSON file, with the line it's on. */
    static final class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    static Object parse(String text) throws ParseException {
        Reader r = new Reader(text);
        r.skipSpace();
        Object value = r.value();
        r.skipSpace();
        if (r.pos < text.length()) {
            throw r.error("unexpected text after the end");
        }
        return value;
    }

    private static final class Reader {
        final String s;
        int pos;

        Reader(String s) {
            this.s = s;
        }

        ParseException error(String what) {
            int line = 1;
            for (int i = 0; i < pos && i < s.length(); i++) {
                if (s.charAt(i) == '\n') {
                    line++;
                }
            }
            return new ParseException("line " + line + ": " + what);
        }

        void skipSpace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        Object value() throws ParseException {
            if (pos >= s.length()) {
                throw error("the file ends too early");
            }
            char c = s.charAt(pos);
            if (c == '{') {
                return object();
            } else if (c == '[') {
                return array();
            } else if (c == '"') {
                return string();
            } else if (s.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else if (s.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            } else if (s.startsWith("null", pos)) {
                pos += 4;
                return null;
            } else if (c == '-' || Character.isDigit(c)) {
                return number();
            }
            throw error("expected a value but found '" + c + "'");
        }

        Map<String, Object> object() throws ParseException {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            pos++;
            skipSpace();
            if (pos < s.length() && s.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipSpace();
                if (pos >= s.length() || s.charAt(pos) != '"') {
                    throw error("expected a \"name\" in quotes");
                }
                String key = string();
                skipSpace();
                expect(':');
                skipSpace();
                map.put(key, value());
                skipSpace();
                if (pos < s.length() && s.charAt(pos) == ',') {
                    pos++;
                } else {
                    expect('}');
                    return map;
                }
            }
        }

        List<Object> array() throws ParseException {
            List<Object> list = new ArrayList<Object>();
            pos++;
            skipSpace();
            if (pos < s.length() && s.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (true) {
                skipSpace();
                list.add(value());
                skipSpace();
                if (pos < s.length() && s.charAt(pos) == ',') {
                    pos++;
                } else {
                    expect(']');
                    return list;
                }
            }
        }

        String string() throws ParseException {
            StringBuilder sb = new StringBuilder();
            pos++;
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') {
                    return sb.toString();
                } else if (c == '\\' && pos < s.length()) {
                    char e = s.charAt(pos++);
                    switch (e) {
                        case 'n':
                            sb.append('\n');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            if (pos + 4 > s.length()) {
                                throw error("bad \\u escape");
                            }
                            sb.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
                            pos += 4;
                            break;
                        default:
                            sb.append(e);
                            break;
                    }
                } else {
                    sb.append(c);
                }
            }
            throw error("a \"text in quotes\" never ends");
        }

        Double number() throws ParseException {
            int start = pos;
            while (pos < s.length() && "+-0123456789.eE".indexOf(s.charAt(pos)) >= 0) {
                pos++;
            }
            try {
                return Double.valueOf(s.substring(start, pos));
            } catch (NumberFormatException e) {
                throw error("'" + s.substring(start, pos) + "' isn't a number");
            }
        }

        void expect(char c) throws ParseException {
            if (pos >= s.length() || s.charAt(pos) != c) {
                throw error("expected '" + c + "'");
            }
            pos++;
        }
    }

    /** Writes a value as JSON, indented, with short lists of numbers kept on one line. */
    static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        write(sb, value, "");
        return sb.append('\n').toString();
    }

    @SuppressWarnings("unchecked")
    private static void write(StringBuilder sb, Object v, String indent) {
        if (v instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) v;
            if (map.isEmpty()) {
                sb.append("{}");
                return;
            }
            boolean small = map.size() <= 8 && isFlat(map.values());
            sb.append(small ? "{" : "{\n");
            int i = 0;
            for (Map.Entry<String, Object> e : map.entrySet()) {
                if (!small) {
                    sb.append(indent).append("  ");
                }
                sb.append(quote(e.getKey())).append(": ");
                write(sb, e.getValue(), indent + "  ");
                if (++i < map.size()) {
                    sb.append(small ? ", " : ",\n");
                }
            }
            sb.append(small ? "}" : "\n" + indent + "}");
        } else if (v instanceof List) {
            List<Object> list = (List<Object>) v;
            boolean small = isFlat(list);
            sb.append(small ? "[" : "[\n");
            for (int i = 0; i < list.size(); i++) {
                if (!small) {
                    sb.append(indent).append("  ");
                }
                write(sb, list.get(i), indent + "  ");
                if (i + 1 < list.size()) {
                    sb.append(small ? ", " : ",\n");
                }
            }
            sb.append(small ? "]" : "\n" + indent + "]");
        } else if (v instanceof String) {
            sb.append(quote((String) v));
        } else if (v instanceof Double || v instanceof Float) {
            double d = ((Number) v).doubleValue();
            sb.append(d == Math.rint(d) && Math.abs(d) < 1e15 ? String.valueOf((long) d) : String.valueOf(d));
        } else {
            sb.append(v);
        }
    }

    /** True if every item is a number, text, true/false, or a list of numbers. */
    private static boolean isFlat(Iterable<?> values) {
        for (Object o : values) {
            if (o instanceof Map) {
                return false;
            }
            if (o instanceof List) {
                for (Object x : (List<?>) o) {
                    if (x instanceof Map || x instanceof List) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.append('"').toString();
    }
}
