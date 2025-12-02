package com.facsculator;

/**
 * Nó da árvore que representa uma operação unária (um operando).
 * Principalmente usado para funções como 'conj(z)'.
 */
public record UnaryOperationNode(TokenType operator, ASTNode operand) implements ASTNode {

    /**
     * Converte para LISP.
     * Formato: (FUNC OPERANDO)
     * Ex: conj(3+4i) -> (conj 3 + 4i)
     */
    @Override
    public String toLispString() {
        String opSymbol = (operator == TokenType.CONJUGATE) ? "conj" : "?";
        return String.format("(%s %s)", opSymbol, operand.toLispString());
    }
}