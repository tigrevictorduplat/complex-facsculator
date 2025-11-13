package com.facsculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Realiza a Análise Léxica (Tokenização).
 * Converte a string de entrada (ex: "(3+4i) * x")
 * em uma lista de Tokens (ex: [LPAREN, COMPLEX, MULTIPLY, VAR, EOF]).
 *
 * Esta é uma implementação de "Tokenizer Inteligente", que tenta
 * agrupar números complexos inteiros (ex: "5.5-2i") em um único token.
 */
public class Tokenizer {

    /** A string de expressão completa que está sendo analisada. */
    private final String input;

    /** O ponteiro (índice) da nossa posição de leitura atual na string 'input'. */
    private int position = 0;

    /**
     * Construtor do Tokenizer.
     * @param input A string de expressão a ser tokenizada.
     */
    public Tokenizer(String input) {
        this.input = input;
    }

    /**
     * Consome a string de entrada, caractere por caractere, e retorna a lista
     * de tokens reconhecidos.
     *
     * @return A lista de Tokens que representa a expressão.
     * @throws Exception Se um caractere inválido ou uma sintaxe inesperada
     * for encontrada.
     */
    public List<Token> tokenize() throws Exception {
        List<Token> tokensList = new ArrayList<>();

        while (position < input.length()) {
            char current = peek(); // Pega o caractere atual

            if (Character.isWhitespace(current)) {
                // 1. Pular espaços em branco
                advance(); // Apenas consome e continua
                continue;
            }

            // 2. Tentar ler um número complexo
            // (ex: "3+4i", "5.5", ".5", "-i", "+3")
            // Inicia se for dígito, ponto (seguido de dígito), ou sinal
            if (Character.isDigit(current) || (current == '.' && Character.isDigit(peekNext()))) {
                tokensList.add(scanNumber());
            } 
            else if (current == '+' || current == '-') {
                // Verifica se o sinal é unário (início de número) ou binário (operação)
                boolean isSign = 
                tokensList.isEmpty() || // Posição inicial
                tokensList.get(tokensList.size() - 1).type() == TokenType.LEFT_PAREN || // Se o sinal está após um '('
                isOperator(tokensList.get(tokensList.size() - 1).type()); // Se o sinal está após outro operador.
                
                if (isSign && (Character.isDigit(peekNext()) || peekNext() == 'i')) {
                    // É um sinal no início de um número (ex: -5, +i)
                    tokensList.add(scanNumber());
                } else {
                    // É um operador de soma/subtração
                    tokensList.add(new Token(current == '+' ? TokenType.PLUS : TokenType.MINUS, String.valueOf(current)));
                    advance();
                }
            }
            // 3. Operadores e Símbolos
            else if (current == '*') {
                if (peekNext() == '*') {
                    // É ** (potência)
                    advance(); // Consome o primeiro '*'
                    advance(); // Consome o segundo '*'
                    tokensList.add(new Token(TokenType.POWER, "**"));
                } else {
                    // É * (multiplicação)
                    tokensList.add(new Token(TokenType.MULTIPLY, "*"));
                    advance();
                }
            } 
            else if (current == '/') {
                tokensList.add(new Token(TokenType.DIVIDE, "/"));
                advance();
            } 
            else if (current == '(') {
                tokensList.add(new Token(TokenType.LEFT_PAREN, "("));
                advance();
            } 
            else if (current == ')') {
                tokensList.add(new Token(TokenType.RIGHT_PAREN, ")"));
                advance();
            }
            else if (current == '[') {
                tokensList.add(new Token(TokenType.LEFT_BRACKET, "["));
                advance();
            }
            else if (current == ']') {
                tokensList.add(new Token(TokenType.RIGHT_BRACKET, "]"));
                advance();
            }

            // 4. Identificadores (Variáveis ou Funções)
            else if (Character.isLetter(current)) {
                // Pode ser 'x', 'conj', 'root', ou 'i'
                tokensList.add(scanIdentifier());
            }
            
            // 5. Erro - Caractere Desconhecido
            else {
                // Retorna uma exceção de erro de tokenização
                throw new Exception("Erro Léxico: Caractere inesperado '" + current + "' na posição " + position);
            }
        }

        // Adiciona um token final para marcar o fim da expressão.
        // Isso facilita a vida do Parser.
        tokensList.add(new Token(TokenType.END_OF_FILE, "<EOF>"));
        return tokensList;
    }

    // --- Métodos Auxiliares de Leitura (Helpers) ---

    /**
     * "Espia" (peek) o caractere na posição atual sem consumí-lo.
     * Como o peek em uma estrutura de pilha.
     *
     * @return O caractere na 'position' atual.
     * Retorna '\0' (nulo) se estivermos no fim da string.
     */
    private char peek() {
        if (position >= input.length()) {
            return '\0'; // Sinaliza o Fim de Arquivo (EOF)
        }
        return input.charAt(position);
    }

    /**
     * "Espia" (peek) o caractere *logo após* a posição atual, sem consumí-lo.
     * Essencial para tokens de múltiplos caracteres (ex: "**").
     *
     * @return O caractere em 'position + 1'.
     * Retorna '\0' (nulo) se o próximo caractere estiver fora dos limites.
     */
    private char peekNext() {
        if (position + 1 >= input.length()) {
            return '\0'; // Sinaliza o Fim de Arquivo (EOF)
        }
        return input.charAt(position + 1);
    }

    /**
     * Consome o caractere atual e avança o ponteiro 'position'
     * para o próximo caractere.
     *
     * @return O caractere que acabou de ser consumido (o que estava em 'peek()').
     */
    private char advance() {
        char current = peek(); // Pega o caractere atual
        if (position < input.length()) {
            position++; // Avança o ponteiro
        }
        return current; // Retorna o caractere que acabamos de passar
    }

    /**
     * Verifica se um tipo de token é um operador binário.
     * Útil para a lógica de 'isSign'.
     */
    private boolean isOperator(TokenType type) {
        return type == TokenType.PLUS || type == TokenType.MINUS ||
               type == TokenType.MULTIPLY || type == TokenType.DIVIDE ||
               type == TokenType.POWER;
    }

    // --- Métodos Auxiliares de Tokenização (Scanners) ---

    /**
     * Lê um identificador (variável ou palavra-chave de função).
     * Começa com uma letra, pode ser seguido por letras ou números.
     */
    private Token scanIdentifier() {
        int start = position; // Marca o início

        // Avança enquanto for uma letra ou dígito (padrão de variável)
        while (Character.isLetterOrDigit(peek())) {
            advance();
        }
        String text = input.substring(start, position);

        // Verifica se é uma palavra-chave (função) ou o 'i' especial
        switch (text) {
            case "conj": return new Token(TokenType.CONJUGATE, text);
            case "root": return new Token(TokenType.ROOT, text);
            case "i":    return new Token(TokenType.COMPLEX_NUMBER, "i"); // 'i' é o número 0+1i
            default:     return new Token(TokenType.VARIABLE, text); // Senão, é uma variável
        }
    }

    /**
     * Lê um número, que pode ser um real (5, 5.5) ou um complexo (3+4i, -i).
     * Este é o método mais complexo do Tokenizer.
     */
    private Token scanNumber() {
        int start = position;

        // 1. Lidar com o sinal inicial (opcional)
        if (peek() == '+' || peek() == '-') {
            advance();
        }

        // 2. Ler a parte numérica (antes do 'i' ou sinal)
        boolean hasDigits = false;
        while (Character.isDigit(peek())) {
            advance();
            hasDigits = true;
        }
        
        // 3. Lidar com a parte decimal (ex: 5.5)
        if (peek() == '.') {
            advance(); // Consome o '.'
            while (Character.isDigit(peek())) {
                advance();
                hasDigits = true;
            }
        }
        
        // 4. Lidar com a parte imaginária (ex: +4i, -i, +i)
        if (peek() == '+' || peek() == '-') {
            // Se o que lemos até agora foi SÓ um sinal (ex: "-"),
            // e o próximo é 'i', então é um número (ex: "-i")
            if (!hasDigits && peekNext() == 'i') {
                advance(); // Consome o sinal
                advance(); // Consome o 'i'
                return new Token(TokenType.COMPLEX_NUMBER, input.substring(start, position));
            }
            
            // Se o que vem depois do sinal é um dígito ou '.',
            // então é a parte numérica do imaginário
            if (Character.isDigit(peekNext()) || peekNext() == '.') {
                advance(); // Consome o sinal (+ ou -)

                // Consome a parte numérica do imaginário
                while (Character.isDigit(peek())) {
                    advance();
                }
                if (peek() == '.') { // Lida com decimal no imaginário
                    advance();
                    while (Character.isDigit(peek())) {
                        advance();
                    }
                }
            } else {
                 // É um número real seguido por um operador '+' ou '-'
                 // Ex: 5+x. O 5 já foi lido.
                 return new Token(TokenType.COMPLEX_NUMBER, input.substring(start, position));
            }
        }

        // 5. Consome o 'i' final (se houver)
        if (peek() == 'i') {
            // Se não lemos nenhum dígito (ex: "i", "+i", "-i")
            if (!hasDigits && (position == start || position == start + 1)) {
                 // Não faça nada aqui, pois o scanIdentifier cuidará do "i"
                 // Mas se for "+i" ou "-i", já foi pego acima.
            }
            advance(); // Consome o 'i'
        }

        // Retorna o token com o texto completo do número
        return new Token(TokenType.COMPLEX_NUMBER, input.substring(start, position));
    }
}