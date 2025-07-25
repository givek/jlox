package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<Token>();

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<String, TokenType>();
    keywords.put("and", TokenType.AND);
    keywords.put("or", TokenType.OR);

    keywords.put("if", TokenType.IF);
    keywords.put("else", TokenType.ELSE);

    keywords.put("for", TokenType.FOR);
    keywords.put("while", TokenType.WHILE);

    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);

    keywords.put("var", TokenType.VAR);
    keywords.put("nil", TokenType.NIL);

    keywords.put("fun", TokenType.FUN);
    keywords.put("return", TokenType.RETURN);

    keywords.put("class", TokenType.CLASS);
    keywords.put("super", TokenType.SUPER);
    keywords.put("this", TokenType.THIS);
  }

  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  private boolean isAtEnd() {
    return this.current >= this.source.length();
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private void addToken(TokenType type) {
    // For tokens which do not have a literal value,
    // e.g., '(', '}', '+', etc.
    addToken(type, null);
  }

  private char advance() {
    return source.charAt(current++);
  }

  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }

    if (source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }

    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }

    return source.charAt(current + 1);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private void identifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }

    String text = source.substring(start, current);
    TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);

    addToken(type);
  }

  private void number() {
    while (isDigit(peek())) {
      advance();
    }

    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the '.'
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }

      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // The closing '"'.
    advance();

    // Trim the surrounding the quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(TokenType.STRING, value);
  }

  private void scanToken() {
    char c = advance();

    switch (c) {
      case '(':
        addToken(TokenType.LEFT_PAREN);
        break;
      case ')':
        addToken(TokenType.RIGHT_PAREN);
        break;

      case '{':
        addToken(TokenType.LEFT_BRACE);
        break;
      case '}':
        addToken(TokenType.RIGHT_BRACE);
        break;

      case ',':
        addToken(TokenType.COMMA);
        break;

      case '.':
        addToken(TokenType.DOT);
        break;

      case '-':
        addToken(TokenType.MINUS);
        break;
      case '+':
        addToken(TokenType.PLUS);
        break;

      case '*':
        addToken(TokenType.STAR);
        break;

      case ';':
        addToken(TokenType.SEMICOLON);
        break;

      case '!':
        addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        break;
      case '<':
        addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        break;
      case '>':
        addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        break;
      case '=':
        addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        break;

      case '/':
        if (match('/')) {

          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) {
            advance();
          }

        } else {
          addToken(TokenType.SLASH);
        }
        break;

      case '"':
        string();
        break;

      case ' ':
      case '\t':
      case '\r':
        // Ignore whitespace
        break;

      case '\n':
        line++;
        break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          Lox.error(line, "Unexpected character: " + c + ".");
        }
        break;
    }
  }

  List<Token> scanTokens() {
    while (!this.isAtEnd()) {
      // We are at the beginning of the next lexeme
      this.start = this.current;
      scanToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line));
    return tokens;
  }
}
