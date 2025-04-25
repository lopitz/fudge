package com.lolplane.fudge.security;

/**
 * Utility class for escaping HTML content to prevent XSS attacks.
 */
public class HTMLEscaper {

    /**
     * Escapes HTML content to prevent XSS attacks.
     *
     * @param content The content to escape
     * @return The escaped content
     */
    public static String escapeHtml(String content) {
        if (content == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
