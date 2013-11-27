package wci.frontend.c.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.c.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.*;
import static wci.frontend.c.CTokenType.*;
import static wci.frontend.c.CErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>WhileStatementParser</h1>
 *
 * <p>Parse a C WHILE statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class WhileStatementParser extends StatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public WhileStatementParser(CParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for DO.
    private static final EnumSet<CTokenType> PAREN_SET =
        StatementParser.STMT_START_SET.clone();
    static {
        PAREN_SET.add(LEFT_PAREN);
        PAREN_SET.add(RIGHT_PAREN);
    }

    /**
     * Parse a WHILE statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the WHILE

        // Create LOOP, TEST, and NOT nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);
        ICodeNode notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

        // The LOOP node adopts the TEST node as its first child.
        // The TEST node adopts the NOT node as its only child.
        loopNode.addChild(breakNode);
        breakNode.addChild(notNode);

        // Synchronize at the LEFT_PAREN.
        token = synchronize(PAREN_SET);
        if (token.getType() == LEFT_PAREN) {
            token = nextToken();  // consume the LEFT_PAREN
        }
        else {
            errorHandler.flag(token, MISSING_LEFT_PAREN, this);
        }
        
        // Parse the expression.
        // The NOT node adopts the expression subtree as its only child.
        ExpressionParser expressionParser = new ExpressionParser(this);
        notNode.addChild(expressionParser.parse(token));

        // Synchronize at the RIGHT_PAREN.
        token = synchronize(PAREN_SET);
        if (token.getType() == RIGHT_PAREN) {
            token = nextToken();  // consume the LEFT_PAREN
        }
        else {
            errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
        }

        // Parse the statement.
        // The LOOP node adopts the statement subtree as its second child.
        StatementParser statementParser = new StatementParser(this);
        loopNode.addChild(statementParser.parse(token));

        return loopNode;
    }
}