package com.craftinginterpreters.lox;

public enum TokenType {
  // Single-character tokens.
  LEFT_PAREN,
  RIGHT_PAREN,

  LEFT_BRACE,
  RIGHT_BRACE,

  COMMA,
  DOT,
  MINUS,
  PLUS,
  SEMICOLON,
  SLASH,
  STAR,

  // One or two character tokens.
  BANG,
  BANG_EQUAL,

  EQUAL,
  EQUAL_EQUAL,

  GREATER,
  GREATER_EQUAL,

  LESS,
  LESS_EQUAL,

  // Literals.
  IDENTIFIER,
  STRING,
  NUMBER,

  // Keywords.
  VAR,

  AND,
  OR,

  TRUE,
  FALSE,

  CLASS,
  THIS,
  SUPER,

  IF,
  ELSE,

  FUN,
  RETURN,

  FOR,
  WHILE,

  PRINT,

  NIL,

  EOF,
}
