package com.facsculator;

/**
 * Interface base para todos os nós da Árvore de Sintaxe Abstrata (AST).
 * Define o comportamento comum que todo nó deve ter.
 */
public interface ASTNode {

    /**
     * Implementa a Notação LISP (List Processing).]
     * 
     * Exemplo: Uma soma de 3 e 4 vira "(+ 3 4)".
     * @return A string formatada em LISP.
     */
    String toLispString();
}
    
