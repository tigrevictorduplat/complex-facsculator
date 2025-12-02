package com.facsculator;

/**
 * Nó da árvore que representa uma variável (ex: x, y, valorA).
 * O valor real desta variável só será resolvido no momento da execução.
 */
public record VariableNode(String name) implements ASTNode {

    /**
     * Converte para LISP.
     * Retorna simplesmente o nome da variável.
     */
    @Override
    public String toLispString() {
        return name;
    }
}