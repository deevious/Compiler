package wci.frontend.c.parsers;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;

import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>BlockParser</h1>
 *
 * <p>Parse a C block.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class BlockParser extends CParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public BlockParser(CParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a block.
     * @param token the initial token.
     * @param routineId the symbol table entry of the routine name.
     * @return the root node of the parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry routineId)
        throws Exception
    {
        DeclarationsParser declarationsParser = new DeclarationsParser(this);
        StatementParser statementParser = new StatementParser(this);

        // Parse any declarations.
        declarationsParser.parse(token, routineId);

        token = synchronize(StatementParser.STMT_START_SET);
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        // Look for the LEFT_BRACE token to parse a compound statement.
        if (tokenType == LEFT_BRACE) {
            rootNode = statementParser.parse(token);
        }

        // Missing LEFT_BRACE: Attempt to parse anyway if possible.
        else {
            errorHandler.flag(token, MISSING_LEFT_BRACE, this);

            if (StatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, RIGHT_BRACE, MISSING_RIGHT_BRACE);
            }
        }

        return rootNode;
    }
}
