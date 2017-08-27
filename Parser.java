/* *** This file is given as part of the programming assignment. *** */

import java.util.ArrayList;
import java.util.Stack;
import java.util.Iterator;

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private Stack<ArrayList<String>> stack = new Stack<ArrayList<String>>();
    private ArrayList<String> vList = new ArrayList<>();
    private int stackSize = 0;
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
    System.out.println("#include <stdio.h>\n");
    System.out.println("int main(void){");
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    private void program() {
	block();
	System.out.println("return 0;");
	System.out.println("}");
    }

    private void block(){
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
    System.out.println("int ");
	mustbe(TK.DECLARE);
	if (is(TK.ID)){
	    for (int i = 0; i < vList.size(); i++) {
	        if (tok.string.equals(vList.get(i))) {
	            System.err.println( "redeclaration of variable " + vList.get(i));
	        }
	    }
	    vList.add(tok.string);
	}
	System.out.println(tok.string);
	mustbe(TK.ID);
	
	while( is(TK.COMMA) ) {
	    System.out.println(", ");
	    scan();
	    if (is(TK.ID)) {
	        for (int i = 0; i < vList.size(); i++) {
	            if (tok.string.equals(vList.get(i))) {
	                System.err.println( "redeclaration of variable " + vList.get(i));
	                break;
	            }
	        }
	        System.out.println(tok.string);
	        vList.add(tok.string);
	    }
	    mustbe(TK.ID);
	    System.out.println(";\n");
	}
 	}

    private void statement_list() {
    while( is(TK.TILDE) || is(TK.ID) || is(TK.PRINT) || is(TK.DO) || is(TK.IF)) {
        
        statement();
    }    
    }
    
    private void statement() {
	if (is(TK.ID) || is(TK.TILDE)) {
	    assignment();
	}
	else if (is(TK.PRINT)){
	    print();
	}
	else if (is(TK.DO)){
	    tk_do();
	}
	else if (is(TK.IF)){
	    tk_if();
	}
	}
	
	private void assignment(){
	    ref_id();
	    System.out.println(" = ");
	    mustbe(TK.ASSIGN);
	    expr();
	    System.out.println(";\n");
	}
	
	private void print() {
	    System.out.println("printf(");
	    mustbe(TK.PRINT);
	    expr();
	    System.out.println(");\n");
	}
	
	private void tk_do() {
	    stack.push(vList);
	    vList = new ArrayList<>();
	    stackSize++;
	    System.out.println("while(");
	    mustbe(TK.DO);
	    guarded_command();
	    vList = new ArrayList<>(stack.pop());
	    stackSize--;
	    System.out.println("}\n");
	    mustbe(TK.ENDDO);
	}
	
	private void tk_if() {
	    stack.push(vList);
	    vList = new ArrayList<>();
	    stackSize++;
	    System.out.println("if(");
	    mustbe(TK.IF);
	    guarded_command();
	    System.out.println("}\n");
	    while (is(TK.ELSEIF)) {
	        System.out.println("else if(");
	        scan();
	        guarded_command();
	    }  
	    System.out.println("}\n");
	    if (is(TK.ELSE)){
	        System.out.println("else{");
	        mustbe(TK.ELSE);
	        block();
	    }
	    System.out.println("}");
	    vList = new ArrayList<>(stack.pop());
	    stackSize--;
	    mustbe(TK.ENDIF);
	    //vList = stack.pop();
	}
	
	private void ref_id() {
	    String token = "";
	    int k = 0;
	    boolean noNum = false;
	    boolean til = false;
	    if(is(TK.TILDE)){
	        System.out.println("_");
	        til = true;
	        token += tok.string;
	        mustbe(TK.TILDE);
	        if(is(TK.NUM)){
	            token += tok.string;
	            k = Integer.parseInt(tok.string);
	            System.out.println(k);
	            mustbe(TK.NUM);
	        }
	        else{
	            noNum = true;
	        }
	    }
	    token += tok.string;
	    if (k > stackSize){
	        System.err.println( "no such variable " + token + " on line " + tok.lineNumber);
	        System.exit(1);
	    }
	    
	    if(is(TK.ID)) {
            boolean hasID = false;
            Iterator<ArrayList<String>> itr = stack.iterator();
            int stackNum = 0;
            while(itr.hasNext()){
                ArrayList<String> iList = new ArrayList<>();
                iList = itr.next();
                for (int i = 0; i < iList.size(); i++) {
                    if (tok.string.equals(iList.get(i))) {
                        hasID = true;
                        //if (til && k != stackNum && noNum == false){
                          //  hasID = false;
                        //}
                    }
                }
                if(noNum == true && stackNum == 0 && hasID == false){
                    System.err.println( "no such variable " + token + " on line " + tok.lineNumber);
                    System.exit(1);
                }
                else if (stack.size() == k){
                    
                }
                else if (til && stackNum < k && hasID == true){
                    hasID = false;
                }
                else if (noNum == false && stackNum == k && hasID == false && til && k > 0){
                    System.err.println( "no such variable " + token + " on line " + tok.lineNumber);
                    System.exit(1);
                }
                stackNum++;
            }
            
            for (int i = 0; i < vList.size(); i++) {
                if (tok.string.equals(vList.get(i))) {
                    //System.out.println("HI " + vList.get(i));
                    hasID = true;
                    }
                }
	        if (hasID == false){
	            
	            System.err.println( tok.string + " is an undeclared variable on line " + tok.lineNumber);
	            System.exit(1);
	        }
        }
	    System.out.println(tok.string);
	    mustbe(TK.ID);
	    
	}
	
	private void guarded_command() {
	    expr();
	    System.out.println(")");
	    mustbe(TK.THEN);
	    System.out.println("{\n");
	    block();
	}
	
	private void expr() {
	    term();
	    while(is(TK.PLUS) || is(TK.MINUS)) {
	        addop();
	        term();
	    }
	}
	
	private void term() {
	    factor();
	    while(is(TK.TIMES) || is(TK.DIVIDE)){
	        multop();
	        factor();
	    }
	}
	
	private void factor() {
	    if(is(TK.LPAREN)) {
	        System.out.println("(");
	        mustbe(TK.LPAREN);
	        expr();
	        System.out.println(")");
	        mustbe(TK.RPAREN);
	    }
	    else if (is(TK.NUM)) {
	        System.out.println(tok.string);
	        mustbe(TK.NUM);
	    }
	    else {
	        ref_id();
	    }
	}
	
	private void addop() {
	    if(is(TK.PLUS)){
	        System.out.println(tok.string);
	        mustbe(TK.PLUS);
	    }
	    else if (is(TK.MINUS)) {
	        System.out.println(tok.string);
	        mustbe(TK.MINUS);
	    }
	}
	
	private void multop(){
	    if (is(TK.TIMES)) {
	        System.out.println(tok.string);
	        mustbe(TK.TIMES);
	    }
	    else if (is(TK.DIVIDE)) {
	        System.out.println(tok.string);
	        mustbe(TK.DIVIDE);
	    }
	}
	
    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }
    
    

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
