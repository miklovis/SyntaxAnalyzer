/**
 * Error and success/failure message generator
 * 
 * @Author: Arnas Miklovis
 */

public class Generate extends AbstractGenerate{
    /** ReportError method used for informing the developer about the error in their code, with information about which token was at fault, which line was it in and what is/was expected instead.
     * 
     * @param token Token type object with information about the parsed piece of text that was not according to grammar
     * @param explanatoryMessage A text of string type containing the information regarding what was expected instead of what has been written
     * @throws CompilationException
     */
    public void reportError(Token token, String explanatoryMessage) throws CompilationException{
        String errorMsg = "";
        if(token.text.length() > 0){    //if token is empty/eof, there will be no instead of, and the exception will just contain information about what did it expect
            errorMsg = "312ERROR " + explanatoryMessage + "instead of " + token.text + " in line " + token.lineNumber;
        }
        else errorMsg = "312ERROR " + explanatoryMessage + "in line " + token.lineNumber;
        
        System.out.println(errorMsg);
        throw new CompilationException(errorMsg);
    }

    
}
