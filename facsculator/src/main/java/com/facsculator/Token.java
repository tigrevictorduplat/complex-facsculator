package com.facsculator;

/**
 * Representa uma unidade léxica (um "token") da expressão.
 * Contém o tipo do token e seu valor (o texto original).
 */
public record Token(
    TokenType type, // O tipo (ex: COMPLEX_NUMBER)
    String text     // O texto original (ex: "3+4i")
) {
    // Podemos adicionar valores opcionais depois,
    // como o ComplexNumber já processado, mas por
    // enquanto o texto é suficiente.
    
    @Override
    public String toString() {
        return String.format("Token[%s, '%s']", type, text);
    }
}