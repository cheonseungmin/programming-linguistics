package programing;

public class Token {

    private static final int KEYWORDS = TokenType.Eof.ordinal(); //키워드는 10개이다.

    private static final String[] reserved = new String[KEYWORDS]; // 예약어 10개
    private static Token[] token = new Token[KEYWORDS];

    public static final Token eofTok = new Token(TokenType.Eof, "<<EOF>>");
    public static final Token boolTok = new Token(TokenType.Bool, "bool");
    public static final Token charTok = new Token(TokenType.Char, "char");
    public static final Token elseTok = new Token(TokenType.Else, "else");
    public static final Token falseTok = new Token(TokenType.False, "false");
    public static final Token floatTok = new Token(TokenType.Float, "float");
    public static final Token ifTok = new Token(TokenType.If, "if");
    public static final Token intTok = new Token(TokenType.Int, "int");
    public static final Token voidTok = new Token(TokenType.Void, "void");
    public static final Token mainTok = new Token(TokenType.Main, "main");
    public static final Token trueTok = new Token(TokenType.True, "true");
    public static final Token whileTok = new Token(TokenType.While, "while");
    
    public static final Token printfTok = new Token(TokenType.Printf, "printf");
    public static final Token scanfTok = new Token(TokenType.Scanf, "scanf");
    public static final Token putTok = new Token(TokenType.Put, "put");
    public static final Token readIntTok = new Token(TokenType.ReadInt, "readInt");
    public static final Token readFloatTok = new Token(TokenType.ReadFloat, "readFloat");
    public static final Token returnTok = new Token(TokenType.Return, "return");
  
    
    public static final Token leftBraceTok = new Token(TokenType.LeftBrace, "{");
    public static final Token rightBraceTok = new Token(TokenType.RightBrace, "}");
    public static final Token leftBracketTok = new Token(TokenType.LeftBracket, "[");
    public static final Token rightBracketTok = new Token(TokenType.RightBracket, "]");
    public static final Token leftParenTok = new Token(TokenType.LeftParen, "(");
    public static final Token rightParenTok = new Token(TokenType.RightParen, ")");
    public static final Token semicolonTok = new Token(TokenType.Semicolon, ";");
    public static final Token commaTok = new Token(TokenType.Comma, ",");
    public static final Token assignTok = new Token(TokenType.Assign, "=");
    public static final Token eqeqTok = new Token(TokenType.Equals, "==");
    public static final Token ltTok = new Token(TokenType.Less, "<");
    public static final Token lteqTok = new Token(TokenType.LessEqual, "<=");
    public static final Token gtTok = new Token(TokenType.Greater, ">");
    public static final Token gteqTok = new Token(TokenType.GreaterEqual, ">=");
    public static final Token notTok = new Token(TokenType.Not, "!");
    public static final Token noteqTok = new Token(TokenType.NotEqual, "!=");
    public static final Token plusTok = new Token(TokenType.Plus, "+");
    public static final Token minusTok = new Token(TokenType.Minus, "-");
    public static final Token multiplyTok = new Token(TokenType.Multiply, "*");
    public static final Token divideTok = new Token(TokenType.Divide, "/");
    public static final Token andTok = new Token(TokenType.And, "&&");
    public static final Token orTok = new Token(TokenType.Or, "||");
    
    public static final Token quoteTok = new Token(TokenType.Quote, "\"");
    public static final Token AmpersandTok = new Token(TokenType.Ampersand, "&");
    public static final Token PercentTok = new Token(TokenType.Percent, "%");

    private TokenType type;
    private String value = "";

    private Token (TokenType t, String v) {
        type = t;
        value = v;
        if (t.compareTo(TokenType.Eof) < 0) { // t의 index가 eof보다 작으면 키워드(예약어)
            int ti = t.ordinal();
            reserved[ti] = v; // 예약어 배열에 값 대입
            token[ti] = this; // 토큰 배열에 자기 자신 집어넣음
        }
    }

    public TokenType type( ) { return type; }

    public String value( ) { return value; }

    public static Token keyword  ( String name ) {
        char ch = name.charAt(0);
        if (ch >= 'A' && ch <= 'Z') return mkIdentTok(name); // 대문자로 시작하는 identifier 토큰 리턴
        for (int i = 0; i < KEYWORDS; i++)
           if (name.equals(reserved[i]))  return token[i]; // name이 예약어이면 reserved 토큰 리턴
        return mkIdentTok(name); // 소문자로 시작하는 identifier 토큰 리턴
    } // keyword

    public static Token mkIdentTok (String name) {
        return new Token(TokenType.Identifier, name);
    }

    public static Token mkIntLiteral (String name) {
        return new Token(TokenType.IntLiteral, name);
    }

    public static Token mkFloatLiteral (String name) {
        return new Token(TokenType.FloatLiteral, name);
    }

    public static Token mkCharLiteral (String name) {
        return new Token(TokenType.CharLiteral, name);
    }

    public String toString ( ) {
        if (type.compareTo(TokenType.Identifier) < 0) return value;
        return type + "\t" + value;
    } // toString

    public static void main (String[] args) {
        System.out.println(eofTok);
        System.out.println(whileTok);
    }
} // Token

