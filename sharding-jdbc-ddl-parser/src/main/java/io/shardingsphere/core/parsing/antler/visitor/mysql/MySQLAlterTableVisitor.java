package io.shardingsphere.core.parsing.antler.visitor.mysql;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import io.shardingsphere.core.parsing.antler.sql.ddl.AlterTableStatement;
import io.shardingsphere.core.parsing.antler.utils.TreeUtils;
import io.shardingsphere.core.parsing.antler.utils.VisitorUtils;
import io.shardingsphere.core.parsing.antler.visitor.AlterTableVisitor;
import io.shardingsphere.core.parsing.parser.token.IndexToken;

public class MySQLAlterTableVisitor extends AlterTableVisitor {
    @Override
    protected void visitPrivateTree(AlterTableStatement statement, ParseTree rootNode) {
        visitAddIndex(statement, rootNode);
        visitDropIndex(statement, rootNode);
        VisitorUtils.visitAddPrimaryKey(statement, rootNode,"addConstraint");
        
        VisitorUtils.visitChangeColumn(statement, rootNode);
        VisitorUtils.parseModifyColumn(statement, rootNode);
        VisitorUtils.parseRenameIndex(statement, rootNode);
        VisitorUtils.parseAddPrimaryKey(statement, rootNode);
        VisitorUtils.parseDropPrimaryKey(statement, rootNode);
    }
    
    
    
    /**
     * Visit add index node.
     * 
     * @param statement
     *            statement parse result
     * @param ancestorNode
     *            ancestor of index node
     * @return indexName node
     */
    protected void visitAddIndex(final AlterTableStatement statement, final ParseTree rootNode) {
        ParserRuleContext indexDefOptionNode = (ParserRuleContext) TreeUtils.getFirstChildByRuleName(rootNode,
                "indexDefOption");
        if (null != indexDefOptionNode) {
            ParserRuleContext indexNameNode = (ParserRuleContext) TreeUtils.getFirstChildByRuleName(indexDefOptionNode,
                    "indexName");
            if (null != indexNameNode) {
                statement.getSqlTokens().add(new IndexToken(indexNameNode.getStart().getStartIndex(),
                        indexNameNode.getText(), statement.getTables().getSingleTableName()));
            }
        }
    }
    
    
    /**
     * Visit drop index node.
     * 
     * @param statement
     *            statement parse result
     * @param ancestorNode
     *            ancestor of index node
     * @return indexName node
     */
    protected void visitDropIndex(final AlterTableStatement statement, final ParseTree ancestorNode) {
        ParserRuleContext dropIndexDefNode = (ParserRuleContext) TreeUtils.getFirstChildByRuleName(ancestorNode,
                "dropIndexDef");
        if (null == dropIndexDefNode) {
            return;
        }

        ParserRuleContext indexNameNode = (ParserRuleContext) dropIndexDefNode
                .getChild(dropIndexDefNode.getChildCount() - 1);
        if (null != indexNameNode) {
            statement.getSqlTokens().add(new IndexToken(indexNameNode.getStart().getStartIndex(),
                    indexNameNode.getText(), statement.getTables().getSingleTableName()));
        }
    }
}
