package com.facsculator;

/**
 * Nó específico para a operação matemática de Radiciação (N-th Root).
 * Representa a estrutura: root[grau](radicando).
 */
public record NthRootNode(int degree, ASTNode radicando) implements ASTNode {

    /**
     * Converte para LISP.
     * Formato: (root[N] EXPRESSAO)
     * Ex: root[2](x) -> (root[2] x)
     */
    @Override
    public String toLispString() {
        return String.format("(root[%d] %s)", degree, radicando.toLispString());
    }
}