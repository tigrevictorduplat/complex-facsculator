package com.facsculator;

import java.util.List;

/**
 * Analisador Sintático (Parser).
 * Responsável por transformar uma lista simples de Tokens em uma
 * Árvore de Sintaxe Abstrata (AST) hierárquica.
 *
 * Utiliza o algoritmo de Recurção Descendente, onde cada nível de
 * precedência gramatical (soma, multiplicação, potência) possui seu próprio método.
 */
public class Parser {

    private final List<Token> tokens;
    private int current = 0; // Ponteiro para o token atual

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Método principal que inicia o parsing.
     * Espera analisar uma expressão completa até o fim do arquivo (EOF).
     * @return O nó raiz da Árvore de Sintaxe Abstrata (AST).
     */
    public ASTNode parse() {
        if (tokens.isEmpty()) return null;
        
        ASTNode ast = expression(); // Começa pelo nível mais baixo (Soma/Subtração)

        // Verificação final: Se terminarmos de analisar a expressão,
        // devemos ter atingido o token EOF. Se sobrar coisa, é erro.
        // Ex: "(3+2) 5" -> Erro, o "5" não deveria estar ali.
        if (!isAtEnd() && peek().type() != TokenType.END_OF_FILE) {
            throw new RuntimeException("Erro de Sintaxe: Token inesperado no final da expressão.");
        }
        
        return ast;
    }

    // --- Níveis de Gramática (Do menor para o maior precedência) ---

    /**
     * Nível 1: Expressão (Soma e Subtração).
     * Regra: term { ( + | - ) term }
     */
    private ASTNode expression() {
        // Primeiro, tentamos pegar um termo (que tem maior prioridade)
        ASTNode left = term();

        // Enquanto houver + ou -, continuamos crescendo a árvore lateralmente
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operatorToken = previous(); // Pega o operador que acabamos de consumir (+ ou -)
            ASTNode right = term(); // Pega o próximo termo
            
            // Cria um novo nó binário combinando o que tínhamos (left) com o novo (right)
            left = new BinaryOperationNode(left, operatorToken.type(), right);
        }

        return left;
    }

    /**
     * Nível 2: Termo (Multiplicação e Divisão).
     * Regra: power { ( * | / ) power }
     */
private ASTNode term() {
        ASTNode left = power(); // Pega o primeiro fator (ex: 2)

        while (true) {
            // Caso 1: Multiplicação/Divisão Explícita (ex: 2 * x)
            if (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
                Token operatorToken = previous();
                ASTNode right = power();
                left = new BinaryOperationNode(left, operatorToken.type(), right);
            }
            
            // Caso 2: Multiplicação Implícita (ex: 2x, 2(x), (a)(b))
            // Se NÃO houver operador, mas o próximo token for o início de outro valor...
            // ...nós assumimos que é uma multiplicação.
            else if (check(TokenType.VARIABLE) ||       // ex: 2x
                     check(TokenType.LEFT_PAREN) ||     // ex: 2(x)
                     check(TokenType.COMPLEX_NUMBER) || // ex: (x)2
                     check(TokenType.CONJUGATE) ||      // ex: 2 conj(z)
                     check(TokenType.ROOT)) {           // ex: 2 root[2](x)
                
                // Criamos o nó de multiplicação, sem consumir token de operador
                ASTNode right = power();
                left = new BinaryOperationNode(left, TokenType.MULTIPLY, right);
            }
            
            // Caso 3: Não é nem explícito nem implícito -> Terminou o termo.
            else {
                break;
            }
        }

        return left;
    }

    /**
     * Nível 3: Potência.
     * Regra: unary { ** unary }
     */
    private ASTNode power() {
        ASTNode left = unary();

        if (match(TokenType.POWER)) {
            Token operatorToken = previous();
            ASTNode right = unary();
            left = new BinaryOperationNode(left, operatorToken.type(), right);
        }

        return left;
    }

    /**
     * Nível 4: Unário e Funções (conj, root).
     * Trata funções especiais e delega para 'primary' se não encontrar nenhuma.
     */
    private ASTNode unary() {
        // Verifica se é a função 'conj'
        if (match(TokenType.CONJUGATE)) {
            consume(TokenType.LEFT_PAREN, "Esperado '(' após 'conj'.");
            ASTNode operand = expression();
            consume(TokenType.RIGHT_PAREN, "Esperado ')' após argumento de 'conj'.");
            return new UnaryOperationNode(TokenType.CONJUGATE, operand);
        }

        // Verifica se é a função 'root'
        // Sintaxe: root[grau](expressão)
        if (match(TokenType.ROOT)) {
            // 1. Ler o grau entre colchetes [n]
            consume(TokenType.LEFT_BRACKET, "Esperado '[' após 'root' para definir o grau da raiz.");
            
            // O grau deve ser um número inteiro positivo
            Token numberToken = consume(TokenType.COMPLEX_NUMBER, "Esperado um número inteiro para o grau da raiz.");
            int degree = parseIntegerDegree(numberToken.text());
            
            consume(TokenType.RIGHT_BRACKET, "Esperado ']' após o grau da raiz.");

            // 2. Ler o radicando entre parênteses (expressão)
            consume(TokenType.LEFT_PAREN, "Esperado '(' antes do radicando.");
            ASTNode radicando = expression();
            consume(TokenType.RIGHT_PAREN, "Esperado ')' após o radicando.");

            return new NthRootNode(degree, radicando);
        }

        return primary();
    }

    /**
     * Nível 5: Primário (O fim da linha).
     * Trata Números, Variáveis e Expressões entre Parênteses.
     */
    private ASTNode primary() {
        // Caso 1: Número Complexo
        if (match(TokenType.COMPLEX_NUMBER)) {
            ComplexNumber value = parseComplexString(previous().text());
            return new NumberNode(value);
        }

        // Caso 2: Variável
        if (match(TokenType.VARIABLE)) {
            return new VariableNode(previous().text());
        }

        // Caso 3: Agrupamento com Parênteses ( Expressão )
        if (match(TokenType.LEFT_PAREN)) {
            ASTNode expr = expression(); // Volta para o topo da hierarquia
            consume(TokenType.RIGHT_PAREN, "Esperado ')' após a expressão.");
            return expr;
        }

        // Se chegou aqui, é erro (Regra 5)
        throw new RuntimeException("Erro de Sintaxe: Token inesperado '" + peek().text() + "'");
    }

    // --- Helpers (Métodos Auxiliares de Navegação) ---

    /**
     * Verifica se o token atual é de algum dos tipos passados.
     * Se for, consome o token (avança) e retorna true.
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Tenta consumir um token de um tipo específico.
     * Se o token atual não for desse tipo, lança uma mensagem de Erro.
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException("Erro de Sintaxe: " + message);
    }

    /** Verifica o tipo do token atual sem consumir. */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    /** Avança o ponteiro e retorna o token que foi consumido. */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /** Retorna true se acabaram os tokens úteis (exceto EOF). */
    private boolean isAtEnd() {
        return peek().type() == TokenType.END_OF_FILE;
    }

    /** Retorna o token atual. */
    private Token peek() {
        return tokens.get(current);
    }

    /** Retorna o token anterior (o último consumido). */
    private Token previous() {
        return tokens.get(current - 1);
    }

    // --- Helpers de Parsing de String para Objeto ---

    /**
     * Helper para converter a string do Tokenizer ("3+4i", "-i") 
     * em um objeto ComplexNumber real.
     */
    private ComplexNumber parseComplexString(String text) {
        text = text.replace(" ", ""); // Limpa espaços
        
        // Caso simples: Variável 'i' isolada
        if (text.equals("i") || text.equals("+i")) return new ComplexNumber(0, 1);
        if (text.equals("-i")) return new ComplexNumber(0, -1);

        // Remove o 'i' para facilitar parsing se ele existir no fim
        boolean hasI = text.endsWith("i");
        String cleanText = hasI ? text.substring(0, text.length() - 1) : text;

        try {
            // Tenta parsear direto se for número real puro
            if (!hasI) {
                return new ComplexNumber(Double.parseDouble(text));
            }
            
            // Lógica para separar parte real e imaginária
            // Procura o último '+' ou '-' para dividir a string
            int splitIndex = -1;
            // Começa do penúltimo char (para ignorar sinal do final se houver erro)
            for (int i = cleanText.length() - 1; i >= 0; i--) {
                char c = cleanText.charAt(i);
                if (c == '+' || c == '-') {
                    // Ignora se for o primeiro char (sinal unário do real)
                    if (i != 0) {
                        splitIndex = i;
                        break;
                    }
                }
            }

            if (splitIndex == -1) {
                // Imaginário puro (ex: "5i", "-2i")
                // Se sobrou só "-" ou "+", é 1 ou -1
                if (cleanText.equals("+")) return new ComplexNumber(0, 1);
                if (cleanText.equals("-")) return new ComplexNumber(0, -1);
                return new ComplexNumber(0, Double.parseDouble(cleanText));
            }

            // Complexo completo (ex: "3+4i")
            String realPart = cleanText.substring(0, splitIndex);
            String imagPart = cleanText.substring(splitIndex); // Inclui o sinal (+4 ou -4)

            // Trata casos como "3+i" (imagPart = "+")
            if (imagPart.equals("+")) imagPart = "1";
            if (imagPart.equals("-")) imagPart = "-1";

            return new ComplexNumber(Double.parseDouble(realPart), Double.parseDouble(imagPart));

        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao processar número complexo: " + text);
        }
    }

    /**
     * Helper para validar o grau da raiz (root[n]).
     */
    private int parseIntegerDegree(String text) {
        try {
            // O lexer pode ter pego "3.0" ou "3".
            double d = Double.parseDouble(text);
            if (d % 1 != 0 || d <= 0) {
                 throw new RuntimeException();
            }
            return (int) d;
        } catch (Exception e) {
            throw new RuntimeException("O grau da raiz deve ser um número inteiro positivo. Encontrado: " + text);
        }
    }
}