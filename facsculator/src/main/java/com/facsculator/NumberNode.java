package com.facsculator;

/**
 * Nó da árvore que representa um valor numérico constante (ComplexNumber).
 * É uma "folha" da árvore (não tem filhos).
 */
public record NumberNode(ComplexNumber value) implements ASTNode {

    /**
     * Converte para LISP.
     * Ex: Se o valor for 3+4i, retorna "3 + 4i".
     */
    @Override
    public String toLispString() {
        // Usa o toString() inteligente do ComplexNumber que já criamos
        return value.toString();
    }
}