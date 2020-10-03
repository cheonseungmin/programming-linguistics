package programing;

import java.io.*;

public class Lexer {

    private boolean isEof = false;
    private char ch = ' '; 
    private BufferedReader input;
    private String line = "";
    private int lineno = 0;
    private int col = 1;
    private final String letters = "abcdefghijklmnopqrstuvwxyz"
        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "0123456789";
    private final char eolnCh = '\n';
    private final char eofCh = '\004';
    

    public Lexer (String fileName) { // source filename
        try {
            input = new BufferedReader (new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        }
    }

    private char nextChar() { // Return next char
        if (ch == eofCh)
            error("Attempt to read past end of file");
        col++;
        if (col >= line.length()) {
            try {
                line = input.readLine( );
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } // try
            if (line == null) // at end of file
                line = "" + eofCh;
            else {
                // System.out.println(lineno + ":\t" + line);
                lineno++;
                line += eolnCh;
            } // if line
            col = 0;
        } // if col
        return line.charAt(col);
    }
            

    public Token next( ) { // Return next token
        do {
            if (isLetter(ch)) { // ident or keyword
                String spelling = concat(letters + digits); //[a-zA-Z0-9]
                return Token.keyword(spelling); // ��������� identifier���� Ȯ��
            } else if (isDigit(ch)) { // int or float literal
                String number = concat(digits);
                if (ch != '.')  // int Literal, ch == white space
                    return Token.mkIntLiteral(number); //.�� �ƴϸ� int
                number += concat(digits);
                if(ch == 'e' || ch =='E') {
                	number += chkE();
                }
                return Token.mkFloatLiteral(number);
            } else switch (ch) {
            case ' ': case '\t': case '\r': case eolnCh:
                ch = nextChar();
                break;
            
            case '/':  // divide or comment
                ch = nextChar();
                if (ch != '/')  return Token.divideTok;
                // comment
                do {
                    ch = nextChar();
                } while (ch != eolnCh);
                ch = nextChar();
                break;
            
            case '\'':  // char literal '\''.'
                char ch1 = nextChar(); // .
                nextChar(); // '
                ch = nextChar(); // white space
                return Token.mkCharLiteral("" + ch1);
                
            case eofCh: return Token.eofTok;
            
            case '+': ch = nextChar();
                return Token.plusTok;
                
            // - * ( ) { } ; ,  student exercise
            ///////////////////////////////////////////////////////////////////////////
            
            case '-': ch = nextChar();
            	return Token.minusTok;
            
            case '*': ch = nextChar();
            	return Token.multiplyTok;
            
            case '(': ch = nextChar();
        		return Token.leftParenTok;
        	
            case ')': ch = nextChar();
    			return Token.rightParenTok;
    			
            case '{': ch = nextChar();
    			return Token.leftBraceTok;
    			
            case '}': ch = nextChar();
    			return Token.rightBraceTok;
    			
            case ';': ch = nextChar();
    			return Token.semicolonTok;
    			
            case ',': ch = nextChar();
    			return Token.commaTok;
    			
    		///////////////////////////////////////////////////////////////////////////
                
            case '&': 
            	return chkOpt('&', Token.AmpersandTok, Token.andTok);
            
            case '|': check('|'); return Token.orTok;

            case '=':
                return chkOpt('=', Token.assignTok,
                                   Token.eqeqTok);
                
                // < > !  student exercise 
            ///////////////////////////////////////////////////////////////////////////
            case '<':
                return chkOpt('<', Token.ltTok, // <
                                   Token.lteqTok); // <=
            case '>':
                return chkOpt('>', Token.gtTok, // >
                                   Token.gteqTok); // >=
            case '!':
                return chkOpt('!', Token.notTok, // !
                                   Token.noteqTok); // !=
            ///////////////////////////////////////////////////////////////////////////
            case '"': ch = nextChar();
				return Token.quoteTok;
			
            case '%': ch = nextChar();
				return Token.PercentTok;

            default:  error("Illegal character " + ch); 
            } // switch
        } while (true);
    } // next


    private boolean isLetter(char c) {
        return (c>='a' && c<='z' || c>='A' && c<='Z');
    }
  
    private boolean isDigit(char c) {
        return (c>='0' && c<='9');  // student exercise
    }

    private void check(char c) {
        ch = nextChar();
        if (ch != c) 
            error("Illegal character, expecting " + c);
        ch = nextChar();
    }

    // student exercise
    private Token chkOpt(char c, Token one, Token two) {
    	String s1 = "";
    	s1 += c;
    	ch = nextChar();
    	s1 += ch;
    	
    	if(s1.equals(two.toString())) {
    		ch = nextChar();
    		return two;
    	}
    	else return one;
    }

    private String concat(String set) {
        String r = "";
        do {
            r += ch;
            ch = nextChar();
        } while (set.indexOf(ch) >= 0);
        return r;
    }

    public void error (String msg) {
        System.err.print(line);
        System.err.println("Error: column " + col + " " + msg);
        System.exit(1);
    }
    
    public String chkE() {
    	String s = "e";
    	ch = nextChar();
    	if(ch == '-') {
    		s += '-';
    		ch = nextChar();
    	}
    	s += concat(digits);
    	
    	return s;
    }

    static public void main ( String[] argv ) {
        Lexer lexer = new Lexer("test.txt");
        Token tok = lexer.next( );
        while (tok != Token.eofTok) {
        	System.out.println(tok);
            tok = lexer.next( );
        }
        
    } // main

}


