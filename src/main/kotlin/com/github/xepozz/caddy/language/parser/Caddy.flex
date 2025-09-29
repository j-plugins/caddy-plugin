/* Caddyfile JFlex Lexer for Pattern-Based Parsing */

package com.github.xepozz.caddy.language.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.github.xepozz.caddy.language.psi.CaddyTypes;
import java.util.Stack;

%%

%class CaddyLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%state YYINITIAL
%state IN_BLOCK
%state IN_HEREDOC

// Common macros
WHITESPACE = [ \t\f]
NEWLINE = \r\n|\r|\n
COMMENT = "#"[^\n]*

// Identifier patterns
IDENTIFIER = [a-zA-Z][a-zA-Z0-9_\-]*
NUMBER = [0-9]+
TEXT = [^\s{\}(\)\[\]<\>\|\#\'\`\-\+\?\@][^\s{\}(\)\[\]<\>]*
SYMBOL = [\-\+\~\?\<\>]
AT = @

// Heredoc patterns
HEREDOC_START = "<<"[a-zA-Z0-9_\-]+
QUOTTED_STRING = "\""(\\\"|[^\"])*"\""
BACKTICK_STRING = "`"(\\\`|[^\`])*"`"

// Special symbols
LBRACE = "{"
RBRACE = "}"
LPAREN = "("
RPAREN = ")"
LBRACKET = "["
RBRACKET = "]"

%{
private Stack<Integer> stack = new Stack<>();
private String heredocMarker = null;
private Integer heredocIndent = null;

public void yypushState(int newState) {
  stack.push(yystate());
  yybegin(newState);
}

public void yypopState() {
  yybegin(stack.pop());
}

public void startHeredoc() {
//  System.out.println("Starting heredoc with marker: " + yytext());
  heredocMarker = yytext().toString().substring(2).trim(); // Remove << and trim
  heredocIndent = null; // Will be determined by first content line
  yypushState(IN_HEREDOC);
}

public void endHeredoc() {
//  System.out.println("Ending heredoc with text: " + yytext());
  String text = yytext().toString();

  String trimmedText = text.trim();
  int markerEnd = trimmedText.indexOf(heredocMarker) + heredocMarker.length();

  int totalLength = text.length();
  int leadingWhitespace = text.length() - text.stripLeading().length();
  int pushbackAmount = totalLength - leadingWhitespace - markerEnd;

  if (pushbackAmount > 0) {
    yypushback(pushbackAmount);
  }

  heredocMarker = null;
  heredocIndent = null;
  yypopState();
}
public boolean isHeredocEnd(String text) {
  if (heredocMarker == null || heredocIndent == null) return false;

  String strippedText = text.stripLeading();
  int lineIndent = text.length() - strippedText.length();

  if (lineIndent != heredocIndent) {
    return false;
  }

  return strippedText.startsWith(heredocMarker);
}

public void determineHeredocIndent(String text) {
  String trimmedText = text.trim();
  if (heredocIndent != null || trimmedText.isEmpty()) {
    return;
  }

  if (!trimmedText.isEmpty()) {
    heredocIndent = text.length() - text.stripLeading().length();
  }
}

public String processHeredocContent(String text) {
  if (heredocIndent == null) {
    return text;
  }

  String newlineChars = text.substring(text.length());

  if (text.length() >= heredocIndent) {
    String indent = text.substring(0, Math.min(heredocIndent, text.length()));
    boolean isValidIndent = indent.matches("[ \t]*");

    if (isValidIndent && text.length() >= heredocIndent) {
      return text.substring(heredocIndent) + newlineChars;
    }
  }

  return text;
}
%}

%%

<YYINITIAL, IN_BLOCK> {
{LBRACE}                                     { yypushState(IN_BLOCK); return CaddyTypes.LBRACE; }
{RBRACE}                                     { yypopState(); return CaddyTypes.RBRACE; }

// Special symbols
{LPAREN}                                     { return CaddyTypes.LPAREN; }
{RPAREN}                                     { return CaddyTypes.RPAREN; }
{LBRACKET}                                   { return CaddyTypes.LBRACKET; }
{RBRACKET}                                   { return CaddyTypes.RBRACKET; }

// Heredoc start
{HEREDOC_START}{NEWLINE}                     { startHeredoc(); return CaddyTypes.HEREDOC_START; }

// Common elements
{IDENTIFIER}                                 { return CaddyTypes.IDENTIFIER; }
{NUMBER}                                     { return CaddyTypes.NUMBER; }
{SYMBOL}                                     { return CaddyTypes.SYMBOL; }
{AT}{TEXT}                                   { return CaddyTypes.MATCHER; }
{TEXT}|{QUOTTED_STRING}|{BACKTICK_STRING}    { return CaddyTypes.TEXT; }

// Whitespace and comments
{WHITESPACE}                                 { return TokenType.WHITE_SPACE; }
{NEWLINE}                                    { return CaddyTypes.EOL; }
{COMMENT}                                    { return CaddyTypes.COMMENT; }
}

<IN_HEREDOC> {
  [^\r\n]*{NEWLINE}                           {
      String text = yytext().toString();

    determineHeredocIndent(text);
      if (isHeredocEnd(text)) {
        endHeredoc();
        return CaddyTypes.HEREDOC_END;
      }

      return CaddyTypes.HEREDOC_CONTENT;
    }
}

// Catch any other character
[^]                                            { return TokenType.BAD_CHARACTER; }
