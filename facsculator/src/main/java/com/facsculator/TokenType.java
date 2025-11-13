package com.facsculator;

/**
 * Enum que define os tipos de "tokens" (peças)
 * que o Tokenizer pode reconhecer.
 */
public enum TokenType {
    // --- Tipos de Valor ---
    COMPLEX_NUMBER, // Um número complexo (ex: 3+4i, 5, -2i)
    VARIABLE,       // Uma variável (ex: x, y, z)

    // --- Operadores ---
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    POWER,          // ** 
    
    // --- Funções (Vamos adicioná-las) ---
    CONJUGATE,      // conj(expressao)
    ROOT,           // root[n](expressao)

    // --- Símbolos ---
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACKET,   // [ (para o índice da raiz)
    RIGHT_BRACKET,  // ]

    // --- Controle ---
    END_OF_FILE     // Fim da expressão (EOF)
}