/**
 * 
 * Syntax analyzer for 312 exercise.
 * 
 * @Author: Arnas Miklovis
 */


import java.io.IOException;
import java.io.PrintStream;

public class SyntaxAnalyser extends AbstractSyntaxAnalyser{
    
    /**Creates a new SyntaxAnalyser which supplies the LexicalAnalyser with the file name and creates a new generator
	 * 
	 * @param string
	 * @throws IOException
	 */
    public SyntaxAnalyser(String string) throws IOException{
        lex = new LexicalAnalyser(string);
        myGenerate = new Generate();
    } //end of constructor method

	/** Non-terminal statement part method of the supplied grammar
	 * <statement part> ::= begin <statement list> end
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
    public void _statementPart_() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("StatementPart");
        
		if(nextToken.symbol == Token.beginSymbol){
			myGenerate.insertTerminal(nextToken);
			_statementList_();

			

			if(nextToken.symbol == Token.endSymbol){
				myGenerate.insertTerminal(nextToken);

				nextToken = lex.getNextToken();

			}
		} else myGenerate.reportError(nextToken, "Expected begin ");
		

        myGenerate.finishNonterminal("StatementPart");
		
		if(nextToken.symbol == Token.eofSymbol){
			myGenerate.insertTerminal(nextToken);
		}
    }

	/** Non-terminal statement list method of the supplied grammar
	 * <statement list> ::= <statement> | <statement list> ; <statement>
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _statementList_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("StatementList");
		nextToken = lex.getNextToken();

		switch (nextToken.symbol) {
			case Token.callSymbol:
			case Token.doSymbol:
			case Token.identifier:
			case Token.ifSymbol:
			case Token.forSymbol:
			case Token.whileSymbol:
				_statement_();
				break;
				
			case Token.endSymbol:
				break;

			default:
				myGenerate.reportError(nextToken, "Expected a statement ");
				break;
		}

		switch (nextToken.symbol) {
			case Token.endSymbol:
				break;
		
			case Token.semicolonSymbol:
				myGenerate.insertTerminal(nextToken);
				_statementList_();
				break;

			default:
				nextToken = lex.getNextToken();
				if(nextToken.symbol == Token.semicolonSymbol){
					myGenerate.insertTerminal(nextToken);
					_statementList_();
				}
				break;
		}

		myGenerate.finishNonterminal("StatementList");
	}


	/** Non-terminal statement method of the supplied grammar
	 * <statement> ::= <assignment statement> | <if statement> | <while statement> | <procedure statement> | <until statement> | <for statement>
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _statement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("Statement");
		System.out.println(nextToken.toString());
		switch (nextToken.symbol) {
			case Token.identifier:
				_assignmentStatement_();
				break;
			case Token.ifSymbol:
				_ifStatement_();
				break;
			case Token.forSymbol:
				_forStatement_();
				break;
			case Token.callSymbol:
				_procedureStatement_();
				break;
			case Token.doSymbol:
				_untilStatement_();
				break;
			case Token.whileSymbol:
				_whileStatement_();
				break;
		
			default:
				break;
		}



		myGenerate.finishNonterminal("Statement");
	}


	/** Non-terminal assignment statement method of the supplied grammar
	 * <assignment statement> ::= identifier := <expression> | identifier := stringConstant
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _assignmentStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("AssignmentStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		if(nextToken.symbol == Token.becomesSymbol){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();
			switch (nextToken.symbol) {
				case Token.stringConstant:
					myGenerate.insertTerminal(nextToken);
					break;
			
				case Token.identifier:
				case Token.leftParenthesis:
				case Token.numberConstant:
					_expression_();
					break;

				default:
					myGenerate.reportError(nextToken, "Expected an expression or a string constant ");
					break;
			}
		}

		myGenerate.finishNonterminal("AssignmentStatement");
	}


	/** Non-terminal if statement method of the supplied grammar
	 * <if statement> ::= if <condition> then <statement list> end if | if <condition> then <statement list> else <statement list> end if
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _ifStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("IfStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		if(nextToken.symbol == Token.identifier){
			_condition_();
			nextToken = lex.getNextToken();

			if(nextToken.symbol == Token.thenSymbol){
				myGenerate.insertTerminal(nextToken);
				_statementList_();
				nextToken = lex.getNextToken();

				if(nextToken.symbol == Token.endSymbol){
					myGenerate.insertTerminal(nextToken);
					nextToken = lex.getNextToken();

					if(nextToken.symbol != Token.ifSymbol){
						myGenerate.reportError(nextToken, "Expected an if ");
					}
				}
				else if(nextToken.symbol == Token.elseSymbol){
					_statementList_();
					nextToken = lex.getNextToken();
					if(nextToken.symbol == Token.endSymbol){
						myGenerate.insertTerminal(nextToken);
						nextToken = lex.getNextToken();
	
						if(nextToken.symbol != Token.ifSymbol){
							myGenerate.reportError(nextToken, "Expected an if ");
						}
						myGenerate.insertTerminal(nextToken);

					}

				} else myGenerate.reportError(nextToken, "Expected an else or an end if identifier ");

			}
			else myGenerate.reportError(nextToken, "Expected a then identifier ");
		}
		else myGenerate.reportError(nextToken, "Expected an identifier ");
		myGenerate.finishNonterminal("IfStatement");
	}


	/** Non-terminal while statement method of the supplied grammar
	 * <while statement> ::= while <condition> loop <statement list> end loop
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _whileStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("WhileStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		if(nextToken.symbol == Token.identifier){
			_condition_();
			
			nextToken = lex.getNextToken();
			
			if(nextToken.symbol == Token.loopSymbol){
				myGenerate.insertTerminal(nextToken);

				_statementList_();

				if(nextToken.symbol == Token.endSymbol) {
					myGenerate.insertTerminal(nextToken);

					nextToken = lex.getNextToken();

					if(nextToken.symbol != Token.loopSymbol){
						myGenerate.reportError(nextToken, "Expected \"loop\" ");
					}
					myGenerate.insertTerminal(nextToken);

				} else myGenerate.reportError(nextToken, "Expected an end statement ");

			}
		} else myGenerate.reportError(nextToken, "Expected an identifier ");
	
		myGenerate.finishNonterminal("WhileStatement");
	}


	/** Non-terminal procedure statement method of the supplied grammar
	 * <procedure statement> ::= call identifier ( <argument list> )
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _procedureStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("ProcedureStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		if(nextToken.symbol == Token.identifier){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();
			
			if(nextToken.symbol == Token.leftParenthesis){
				myGenerate.insertTerminal(nextToken);
				nextToken = lex.getNextToken();

				if(nextToken.symbol == Token.identifier){
					_argumentList_();

					if(nextToken.symbol != Token.rightParenthesis){
						myGenerate.reportError(nextToken, "Expected a ) ");
					}
					myGenerate.insertTerminal(nextToken);

				} else myGenerate.reportError(nextToken, "Expected an argument or an argument list ");
			} else myGenerate.reportError(nextToken, "Expected a ( ");
		} else myGenerate.reportError(nextToken, "Expected an identifier ");
		
		myGenerate.finishNonterminal("ProcedureStatement");
	}

	/** Non-terminal do until statement method of the supplied grammar
	 * <until statement> ::= do <statement list> until <condition>
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _untilStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("UntilStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		switch (nextToken.symbol) {
			case Token.callSymbol:
			case Token.doSymbol:
			case Token.identifier:
			case Token.ifSymbol:
			case Token.forSymbol:
			case Token.whileSymbol:
				_statementList_();
				break;
				
		
			default:
				myGenerate.reportError(nextToken, "Expected a statement ");
				break;
		}

		nextToken = lex.getNextToken();
		
		if(nextToken.symbol == Token.untilSymbol){
			myGenerate.insertTerminal(nextToken);

			nextToken = lex.getNextToken();
			if(nextToken.symbol == Token.identifier){
				_condition_();
			}
			else myGenerate.reportError(nextToken, "Expected an identifier");
		}
		myGenerate.finishNonterminal("UntilStatement");
	}


	/** Non-terminal for statement method of the supplied grammar
	 * <for statement> ::= for ( <assignment statement> ; <condition> ; <assignment statement> ) do <statement list> end loop
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _forStatement_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("ForStatement");
		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();

		if(nextToken.symbol == Token.leftParenthesis){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();

			if(nextToken.symbol == Token.identifier){
				_assignmentStatement_();

				if(nextToken.symbol == Token.semicolonSymbol){
					myGenerate.insertTerminal(nextToken);

					nextToken = lex.getNextToken();
					if(nextToken.symbol == Token.identifier){
						_condition_();

						nextToken = lex.getNextToken();
						if(nextToken.symbol == Token.semicolonSymbol){
							myGenerate.insertTerminal(nextToken);

							nextToken = lex.getNextToken();
							if(nextToken.symbol == Token.identifier){
								_assignmentStatement_();

								if(nextToken.symbol == Token.rightParenthesis){
									myGenerate.insertTerminal(nextToken);
									nextToken = lex.getNextToken();

									if(nextToken.symbol == Token.doSymbol){
										myGenerate.insertTerminal(nextToken);
										_statementList_();

										if(nextToken.symbol == Token.endSymbol){
											myGenerate.insertTerminal(nextToken);
											nextToken = lex.getNextToken();

											if(nextToken.symbol == Token.loopSymbol){
												myGenerate.insertTerminal(nextToken);
											} else myGenerate.reportError(nextToken, "Expected \"loop\" ");
										} else myGenerate.reportError(nextToken, "Expected end loop indicator ");
									} else myGenerate.reportError(nextToken, "Expected do ");
								} else myGenerate.reportError(nextToken, "Expected ) ");
							} else myGenerate.reportError(nextToken, "Expected an identifier ");
						} else myGenerate.reportError(nextToken, "Expected a semicolon ");
					} else myGenerate.reportError(nextToken, "Expected an identifier ");
				} else myGenerate.reportError(nextToken, "Expected a semicolon ");
			} else myGenerate.reportError(nextToken, "Expected an identifier ");
		} else myGenerate.reportError(nextToken, "Expected ( ");

		myGenerate.finishNonterminal("ForStatement");
	}

	/** Non-terminal expression method of the supplied grammar
	 * <expression> ::= <term> | <expression> + <term> | <expression> - <term>
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _expression_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("Expression");
		
		_term_();

		if(nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();
			_expression_();
		}

		myGenerate.finishNonterminal("Expression");
	}

	/** Non-terminal term method of the supplied grammar
	 * <term> ::= <factor> | <term> * <factor> | <term> / <factor>
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _term_() throws IOException, CompilationException{
		myGenerate.commenceNonterminal("Term");

		_factor_();

		nextToken = lex.getNextToken();
		
		if(nextToken.symbol == Token.timesSymbol || nextToken.symbol == Token.divideSymbol){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();
			_term_();
		}

		myGenerate.finishNonterminal("Term");
	}


	/** Non-terminal factor method of the supplied grammar
	 * <factor> ::= identifier | numberConstant | ( <expression> )
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _factor_() throws IOException, CompilationException{
		myGenerate.commenceNonterminal("Factor");


		switch (nextToken.symbol) {
			case Token.identifier:
			case Token.numberConstant:
				myGenerate.insertTerminal(nextToken);
				break;

			case Token.leftParenthesis:
				myGenerate.insertTerminal(nextToken);
				nextToken = lex.getNextToken();
				_expression_();
				if(nextToken.symbol == Token.rightParenthesis){
					myGenerate.insertTerminal(nextToken);
				} else myGenerate.reportError(nextToken, "Expected a ) ");
				break;


			default:
				break;
		}

		myGenerate.finishNonterminal("Factor");
	}


	/** Non-terminal condition method of the supplied grammar
	 * <condition> ::= identifier <conditional operator> identifier | identifier <conditional operator> numberConstant | identifier <conditional operator> stringConstant
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _condition_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("Condition");
		myGenerate.insertTerminal(nextToken);

		_conditionalOperator_();

		nextToken = lex.getNextToken();
		
		switch (nextToken.symbol) {
			case Token.identifier:
			case Token.numberConstant:
			case Token.stringConstant:
				myGenerate.insertTerminal(nextToken);
				break;
		
			default:
				myGenerate.reportError(nextToken, "Expected an identifier, a number constant or a string constant ");
				break;
		}
		
		myGenerate.finishNonterminal("Condition");
	}


	/** Non-terminal conditional operator method of the supplied grammar
	 * <conditional operator> ::= > | >= | = | /= | < | <=
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _conditionalOperator_() throws IOException, CompilationException {
		myGenerate.commenceNonterminal("ConditionalOperator");
		
		nextToken = lex.getNextToken();

		switch (nextToken.symbol) {
			case Token.greaterThanSymbol:
			case Token.greaterEqualSymbol:
			case Token.equalSymbol:
			case Token.notEqualSymbol:
			case Token.lessThanSymbol:
			case Token.lessEqualSymbol:
				myGenerate.insertTerminal(nextToken);
				break;
		
			default:
				myGenerate.reportError(nextToken, "Expected a conditional operator ");
				break;
		}
		myGenerate.finishNonterminal("ConditionalOperator");
	}


	/** Non-terminal argument list operator method of the supplied grammar
	 * <argument list> ::= identifier | <argument list> , identifier
	 * 
	 * 
	 * @throws IOException if any read errors occur during parsing
	 * @throws CompilationException if there are any errors in the syntax
	 */
	public void _argumentList_() throws IOException, CompilationException{
		myGenerate.commenceNonterminal("ArgumentList");

		myGenerate.insertTerminal(nextToken);
		nextToken = lex.getNextToken();
		System.out.println(nextToken.toString());
		if(nextToken.symbol == Token.commaSymbol){
			myGenerate.insertTerminal(nextToken);
			nextToken = lex.getNextToken();
			if(nextToken.symbol == Token.identifier){
				_argumentList_();
			} else myGenerate.reportError(nextToken, "Expected an identifier ");
		}
		
		myGenerate.finishNonterminal("ArgumentList");
	}

    public void acceptTerminal(int symbol) throws IOException, CompilationException{

    }

    /** Parses the given PrintStream with this instance's LexicalAnalyser.
		
	  @param ps The PrintStream object to read tokens from.
	  @throws IOException in the event that the PrintStream object can no longer read.
	*/
	public void parse( PrintStream ps ) throws IOException
	{
		myGenerate = new Generate();
		try {
			nextToken = lex.getNextToken() ;
			_statementPart_() ;
			acceptTerminal(Token.eofSymbol) ;
			myGenerate.reportSuccess() ;
		}
		catch( CompilationException ex )
		{
			ps.println( "Compilation Exception" );
			ps.println( ex.toTraceString() );
		}
	} // end of method parse

}
