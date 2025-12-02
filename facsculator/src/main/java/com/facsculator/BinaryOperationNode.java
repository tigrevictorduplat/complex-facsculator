package com.facsculator;

/**
 * Nó da árvore que representa uma operação binária (dois operandos).
 * Ex: Soma (+), Subtração (-), Multiplicação (*), Divisão (/), Potência (**).
 */
public record BinaryOperationNode(ASTNode left, TokenType operator, ASTNode right) implements ASTNode {

    /**
     * Converte para LISP.
     * Formato: (OPERADOR ESQUERDA DIREITA)
     * Ex: 3 + 4 -> (+ 3 4)
     */
    @Override
    public String toLispString() {
        String opSymbol = switch (operator) {
            case PLUS -> "+";
            case MINUS -> "-";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
            case POWER -> "**";
            default -> "?";
        };

        // Recursivamente chama toLispString() dos filhos
        return String.format("(%s %s %s)", opSymbol, left.toLispString(), right.toLispString());
    }
}