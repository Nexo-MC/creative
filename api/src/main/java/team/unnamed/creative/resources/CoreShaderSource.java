package team.unnamed.creative.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class CoreShaderSource {
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_]\\w*");
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z_]\\w*|(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?[fFuU]?|<<=|>>=|\\+\\+|--|[+*/%=!<>|&^-]=|&&|\\|\\||<<|>>|[^\\s]");
    private static final Pattern CONDITIONAL = Pattern.compile("#\\s*(if|ifdef|ifndef)\\b.*", Pattern.DOTALL);
    private static final Pattern BRANCH = Pattern.compile("#\\s*(else|elif)\\b.*");
    private static final Pattern ENDIF = Pattern.compile("#\\s*endif\\b.*");
    private static final Pattern HEADER = Pattern.compile("#\\s*(version|extension)\\b.*");
    private static final Pattern DIRECTIVE = Pattern.compile("#\\s*(\\w+)\\s*([^\\s(]*)");
    private static final Pattern ALPHA_DISCARD = Pattern.compile("if \\( (\\w+) \\. [aw] (?:<|<=|==) [0-9.]+ \\) \\{? discard ; \\}?");
    private static final Pattern HOOK = Pattern.compile("[A-Za-z_]\\w* \\( \\) ;");

    private CoreShaderSource() {
    }

    static String merge(final String existing, final String incoming, final boolean generated) {
        final List<Section> left = sections(existing);
        final List<Section> right = sections(incoming);
        if (signatures(left).equals(signatures(right))) return incoming;

        final List<Section> leftMains = left.stream().filter(section -> section.main).toList();
        final List<Section> rightMains = right.stream().filter(section -> section.main).toList();
        require(leftMains.size() <= 1 && rightMains.size() <= 1, "multiple shader entry points");
        require(leftMains.isEmpty() == rightMains.isEmpty(), "incompatible entry points");
        final List<Section> headers = mergeHeaders(left.stream().filter(section -> !section.main).toList(), right.stream().filter(section -> !section.main).toList());
        final List<String> result = new ArrayList<>();
        headers.stream().filter(section -> HEADER.matcher(section.tokens.getFirst().value).matches()).map(section -> section.source).forEach(result::add);
        headers.stream().filter(section -> !HEADER.matcher(section.tokens.getFirst().value).matches()).map(section -> section.source).forEach(result::add);

        if (!leftMains.isEmpty()) {
            final List<Section> leftBody = body(leftMains.getFirst());
            final List<Section> rightBody = body(rightMains.getFirst());
            final List<Section> merged;
            if (generated && !rightBody.isEmpty() && HOOK.matcher(rightBody.getLast().signature).matches()) {
                int start = rightBody.size() - 1;
                while (start > 0 && HOOK.matcher(rightBody.get(start - 1).signature).matches()) start--;
                require(start > 0, "missing core shader initialization");
                final List<Section> hooks = rightBody.subList(start, rightBody.size());
                final Set<String> signatures = new HashSet<>(signatures(hooks));
                merged = new ArrayList<>(leftBody.stream().filter(section -> !signatures.contains(section.signature)).toList());
                final Section anchor = rightBody.get(start - 1);
                int index = -1;
                for (int i = 0; i < merged.size(); i++) {
                    final Section section = merged.get(i);
                    if (section.signature.equals(anchor.signature) || !keys(anchor).isEmpty() && keys(section).equals(keys(anchor))) index = i;
                }
                require(index >= 0, "missing core shader initialization");
                merged.addAll(index + 1, hooks);
            } else if (generated && rightBody.stream().anyMatch(section -> section.source.contains("gl_Position.y") && section.source.contains("vertexColor.a"))) {
                merged = mergeStatements(leftBody, rightBody.stream().filter(section -> section.tokens.getFirst().value.equals("if")).toList(), false);
            } else {
                merged = mergeStatements(leftBody, rightBody, generated);
            }
            result.add("void main() {\n" + merged.stream().map(section -> section.source).collect(Collectors.joining("\n")) + "\n}");
        }
        return String.join("\n\n", result) + "\n";
    }

    private static List<Section> mergeHeaders(final List<Section> left, final List<Section> right) {
        final List<Section> result = new ArrayList<>(left);
        for (final Section section : right) {
            if (result.stream().anyMatch(previous -> previous.signature.equals(section.signature))) continue;
            if (section.conditional) {
                final Section previous = result.stream().filter(candidate -> candidate.conditional && candidate.tokens.getFirst().value.equals(section.tokens.getFirst().value)).findFirst().orElse(null);
                if (previous != null) {
                    final List<Branch> a = branches(previous);
                    final List<Branch> b = branches(section);
                    require(a.stream().map(Branch::directive).toList().equals(b.stream().map(Branch::directive).toList()), "conflicting shader conditions");
                    final List<String> merged = new ArrayList<>();
                    for (int i = 0; i < a.size(); i++) {
                        merged.add(a.get(i).directive + "\n" + mergeHeaders(a.get(i).sections, b.get(i).sections).stream().map(value -> value.source).collect(Collectors.joining("\n")));
                    }
                    result.set(result.indexOf(previous), sections(String.join("\n", merged) + "\n#endif").getFirst());
                    continue;
                }
            }
            final Set<String> definitions = new HashSet<>();
            result.forEach(previous -> definitions.addAll(keys(previous)));
            for (final String key : keys(section)) require(!definitions.contains(key), "conflicting declaration " + key);
            result.add(section);
        }
        return result;
    }

    private static List<Branch> branches(final Section section) {
        final List<Branch> result = new ArrayList<>();
        int depth = 0;
        int start = section.tokens.getFirst().end;
        String directive = section.tokens.getFirst().value;
        for (final Token token : section.tokens) {
            if (CONDITIONAL.matcher(token.value).matches()) depth++;
            if (ENDIF.matcher(token.value).matches()) depth--;
            if (depth == 1 && BRANCH.matcher(token.value).matches() || depth == 0) {
                result.add(new Branch(directive, sections(section.source.substring(start, token.start))));
                directive = token.value;
                start = token.end;
            }
        }
        return result;
    }

    private static List<Section> body(final Section section) {
        final int start = section.tokens.stream().filter(token -> token.value.equals("{")).findFirst().orElseThrow().end;
        final int end = section.tokens.stream().filter(token -> token.value.equals("}")).reduce((first, second) -> second).orElseThrow().start;
        return sections(section.source.substring(start, end));
    }

    private static List<Section> mergeStatements(final List<Section> left, final List<Section> right, final boolean generated) {
        require((long) left.size() * right.size() <= 1_000_000, "shader is too complex to merge");
        final int[][] lengths = new int[left.size() + 1][right.size() + 1];
        for (int i = left.size() - 1; i >= 0; i--) for (int j = right.size() - 1; j >= 0; j--) {
            lengths[i][j] = left.get(i).signature.equals(right.get(j).signature) ? lengths[i + 1][j + 1] + 1 : Math.max(lengths[i + 1][j], lengths[i][j + 1]);
        }
        final List<Section> result = new ArrayList<>();
        final List<Section> pendingLeft = new ArrayList<>();
        final List<Section> pendingRight = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).signature.equals(right.get(j).signature)) {
                flush(result, pendingLeft, pendingRight, generated);
                result.add(left.get(i++));
                j++;
            } else if (lengths[i + 1][j] >= lengths[i][j + 1]) pendingLeft.add(left.get(i++));
            else pendingRight.add(right.get(j++));
        }
        pendingLeft.addAll(left.subList(i, left.size()));
        pendingRight.addAll(right.subList(j, right.size()));
        flush(result, pendingLeft, pendingRight, generated);
        return result;
    }

    private static void flush(final List<Section> result, final List<Section> left, final List<Section> right, final boolean generated) {
        final Set<String> discarded = new HashSet<>();
        if (generated) right.stream().map(section -> section.alphaDiscard).filter(Objects::nonNull).forEach(discarded::add);
        final List<Section> remaining = left.stream().filter(section -> !discarded.contains(section.alphaDiscard)).toList();
        final Set<String> declarations = new LinkedHashSet<>();
        remaining.forEach(section -> declarations.addAll(keys(section)));
        final Set<String> incoming = new HashSet<>();
        right.forEach(section -> incoming.addAll(keys(section)));
        declarations.retainAll(incoming);
        require(declarations.isEmpty(), "conflicting statements for " + String.join(", ", declarations));
        require(remaining.stream().noneMatch(section -> section.signature.equals("return ;")) || right.isEmpty(), "conflicting early return");
        result.addAll(remaining);
        result.addAll(right);
        left.clear();
        right.clear();
    }

    private static List<String> keys(final Section section) {
        List<String> values = section.tokens.stream().map(Token::value).toList();
        while (!values.isEmpty() && values.getFirst().equals("layout")) {
            final int close = values.indexOf(")");
            require(close >= 0, "invalid shader layout");
            values = values.subList(close + 1, values.size());
        }
        final String first = values.getFirst();
        if (first.startsWith("#")) {
            final Matcher directive = DIRECTIVE.matcher(first);
            if (!directive.find()) return List.of();
            return switch (directive.group(1)) {
                case "version" -> List.of("#version");
                case "define", "extension" -> List.of("#" + directive.group(1) + " " + directive.group(2));
                case "if", "ifdef", "ifndef" -> List.of(first.trim());
                default -> List.of();
            };
        }
        final int open = values.indexOf("(");
        if (open > 0 && values.subList(0, open).stream().noneMatch(value -> Set.of("=", "if", "for", "while", "switch", "layout").contains(value))) {
            final int close = values.indexOf(")");
            final String parameters = String.join(" ", values.subList(open + 1, close));
            final List<String> signatures = new ArrayList<>();
            for (final String parameter : parameters.split(",", -1)) {
                final List<String> parts = new ArrayList<>(List.of(parameter.trim().split("\\s+")));
                parts.removeIf(value -> Set.of("in", "out", "inout", "const", "highp", "mediump", "lowp").contains(value));
                if (parts.size() > 1 && IDENTIFIER.matcher(parts.get(1)).matches()) parts.remove(1);
                signatures.add(String.join(" ", parts));
            }
            return List.of(values.get(open - 1) + "(" + String.join(",", signatures) + ")");
        }
        if (first.equals("struct")) return List.of("struct " + (values.size() > 1 ? values.get(1) : null));
        if (section.conditional || Set.of("if", "for", "while", "switch", "do", "return", "discard", "{").contains(first)) return List.of();
        final Set<String> names = new LinkedHashSet<>();
        final List<String> prefix = new ArrayList<>();
        int depth = 0;
        boolean assigned = false;
        for (final String value : values) {
            if (depth == 0 && Set.of("=", ";", ",").contains(value) && !assigned) {
                final int bracket = prefix.indexOf("[");
                final List<String> declaration = bracket < 0 ? prefix : prefix.subList(0, bracket);
                final String name = declaration.isEmpty() ? null : declaration.contains(".") ? declaration.getFirst() : declaration.getLast();
                if (name != null && IDENTIFIER.matcher(name).matches() && prefix.stream().noneMatch(part -> Set.of("+=", "-=", "*=", "/=", "(").contains(part))) names.add(name);
                assigned = true;
            }
            if (Set.of("(", "[", "{").contains(value)) depth++;
            if (Set.of(")", "]", "}").contains(value)) depth--;
            if (depth == 0 && value.equals(",")) {
                prefix.clear();
                assigned = false;
            } else if (!assigned) prefix.add(value);
        }
        return new ArrayList<>(names);
    }

    private static List<Section> sections(final String source) {
        final List<Token> tokens = tokenize(source);
        final List<Section> result = new ArrayList<>();
        int start = 0;
        int braces = 0;
        int parentheses = 0;
        int conditions = 0;
        for (int index = 0; index < tokens.size(); index++) {
            final Token token = tokens.get(index);
            final String value = token.value;
            if (CONDITIONAL.matcher(value).matches()) conditions++;
            if (ENDIF.matcher(value).matches()) conditions--;
            switch (value) {
                case "{" -> braces++;
                case "}" -> braces--;
                case "(" -> parentheses++;
                case ")" -> parentheses--;
            }
            require(braces >= 0 && parentheses >= 0 && conditions >= 0, "unbalanced shader source");
            if (braces != 0 || parentheses != 0 || conditions != 0) continue;
            final String next = index + 1 < tokens.size() ? tokens.get(index + 1).value : "";
            final boolean end = value.equals(";") || value.startsWith("#") || value.equals("}") && !Set.of(";", "else").contains(next) && !tokens.get(start).value.equals("do") && !isBlockDeclaration(tokens.subList(start, index));
            if (end) {
                final int offset = tokens.get(start).start;
                final String text = source.substring(offset, token.end);
                result.add(new Section(text, tokens.subList(start, index + 1).stream().map(part -> new Token(part.value, part.start - offset, part.end - offset)).toList()));
                start = index + 1;
            }
        }
        require(braces == 0 && parentheses == 0 && conditions == 0 && start == tokens.size(), "incomplete shader source");
        return result;
    }

    private static boolean isBlockDeclaration(final List<Token> tokens) {
        int depth = 0;
        for (final Token token : tokens) {
            if (token.value.equals("{")) break;
            if (token.value.equals("(")) depth++;
            if (token.value.equals(")")) depth--;
            if (depth == 0 && Set.of("struct", "uniform", "buffer", "in", "out", "=").contains(token.value)) return true;
        }
        return false;
    }

    private static List<Token> tokenize(final String source) {
        final List<Token> result = new ArrayList<>();
        int index = 0;
        while (index < source.length()) {
            if (Character.isWhitespace(source.charAt(index)) || source.charAt(index) == '\uFEFF') index++;
            else if (source.startsWith("//", index)) {
                final int end = source.indexOf('\n', index);
                index = end < 0 ? source.length() : end;
            } else if (source.startsWith("/*", index)) {
                final int end = source.indexOf("*/", index + 2);
                require(end >= 0, "unterminated shader comment");
                index = end + 2;
            } else if (source.charAt(index) == '#') {
                int end = index;
                do {
                    final int newline = source.indexOf('\n', end + 1);
                    end = newline < 0 ? source.length() : newline;
                } while (source.substring(index, end).replaceAll("\\r+$", "").endsWith("\\") && end < source.length());
                result.add(new Token(source.substring(index, end).trim(), index, end));
                index = end;
            } else {
                final Matcher matcher = TOKEN.matcher(source).region(index, source.length());
                require(matcher.lookingAt(), "invalid shader token");
                result.add(new Token(matcher.group(), index, matcher.end()));
                index = matcher.end();
            }
        }
        return result;
    }

    private static List<String> signatures(final List<Section> sections) {
        return sections.stream().map(section -> section.signature).toList();
    }

    private static void require(final boolean condition, final String message) {
        if (!condition) throw new MergeException(message);
    }

    private record Token(String value, int start, int end) {
    }

    private record Branch(String directive, List<Section> sections) {
    }

    private static final class Section {
        private final String source;
        private final List<Token> tokens;
        private final String signature;
        private final boolean main;
        private final boolean conditional;
        private final String alphaDiscard;

        private Section(final String source, final List<Token> tokens) {
            this.source = source;
            this.tokens = tokens;
            signature = tokens.stream().map(token -> token.value.replaceAll("\\s+", " ").trim()).collect(Collectors.joining(" "));
            main = tokens.stream().limit(3).map(Token::value).toList().equals(List.of("void", "main", "("));
            conditional = CONDITIONAL.matcher(tokens.getFirst().value).matches();
            final Matcher discard = ALPHA_DISCARD.matcher(signature);
            alphaDiscard = discard.matches() ? discard.group(1) : null;
        }
    }
}
