package com.bazaarehunar.patterns.behavioral.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds the expression tree from query text (recursive descent).
 * <pre>
 * expr   := term ( OR term )*
 * term   := factor ( AND factor )*      (AND is optional: "a b" means "a AND b")
 * factor := NOT factor | ( expr ) | field operator value
 * </pre>
 * Operators: <code>:</code> (contains / equals), <code>&lt; &lt;= &gt; &gt;= =</code> for numbers.
 * Values with spaces can be quoted: <code>name:"blue pottery"</code>.
 */
public class SearchQueryParser {
    private static final Pattern CONDITION = Pattern.compile("^([A-Za-z]+)(<=|>=|<|>|=|:)(.+)$");

    private List<String> tokens;
    private int position;

    public Expression parse(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query is empty");
        }
        tokens = tokenize(query);
        position = 0;
        Expression result = parseOr();
        if (position < tokens.size()) {
            throw new IllegalArgumentException("Unexpected token: " + tokens.get(position));
        }
        return result;
    }

    private Expression parseOr() {
        Expression left = parseAnd();
        while (peekIs("OR")) {
            position++;
            left = new OrExpression(left, parseAnd());
        }
        return left;
    }

    private Expression parseAnd() {
        Expression left = parseFactor();
        while (position < tokens.size() && !peekIs("OR") && !peekIs(")")) {
            if (peekIs("AND")) position++;
            left = new AndExpression(left, parseFactor());
        }
        return left;
    }

    private Expression parseFactor() {
        if (position >= tokens.size()) throw new IllegalArgumentException("Unexpected end of query");
        String token = tokens.get(position++);
        if (token.equalsIgnoreCase("NOT")) return new NotExpression(parseFactor());
        if (token.equals("(")) {
            Expression inner = parseOr();
            if (!peekIs(")")) throw new IllegalArgumentException("Missing closing parenthesis");
            position++;
            return inner;
        }
        return parseCondition(token);
    }

    private Expression parseCondition(String token) {
        Matcher matcher = CONDITION.matcher(token);
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid condition: " + token);
        String field = matcher.group(1);
        String operator = matcher.group(2);
        String value = matcher.group(3).replace("\"", "");
        boolean numericField = List.of("price", "rating", "reviews").contains(field.toLowerCase());
        if (numericField) {
            try {
                return new NumberComparisonExpression(field, operator, Double.parseDouble(value));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Not a number: " + value);
            }
        }
        if (!operator.equals(":")) throw new IllegalArgumentException("Text fields only support ':' (" + token + ")");
        return new TextFieldExpression(field, value);
    }

    private boolean peekIs(String value) {
        return position < tokens.size() && tokens.get(position).equalsIgnoreCase(value);
    }

    private static List<String> tokenize(String query) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (char c : query.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (!inQuotes && (Character.isWhitespace(c) || c == '(' || c == ')')) {
                if (current.length() > 0) { result.add(current.toString()); current.setLength(0); }
                if (c == '(' || c == ')') result.add(String.valueOf(c));
            } else {
                current.append(c);
            }
        }
        if (inQuotes) throw new IllegalArgumentException("Unclosed quote in query");
        if (current.length() > 0) result.add(current.toString());
        return result;
    }
}
