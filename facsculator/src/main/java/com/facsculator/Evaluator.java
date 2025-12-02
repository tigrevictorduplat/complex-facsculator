package com.facsculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Avaliador de Árvore (Evaluator).
 * Percorre a AST recursivamente para calcular o resultado final.
 * Responsável por implementar a Execução de Entradas e a Resolução de Variáveis.
 */
public class Evaluator {

    // "Memória" da calculadora: guarda os valores das variáveis (ex: x = 3+2i)
    private final Map<String, ComplexNumber> variables;
    
    // Scanner para ler entrada do usuário (caso precise pedir valor de variável)
    private final Scanner scanner;

    public Evaluator(Scanner scanner) {
        this.scanner = scanner;
        this.variables = new HashMap<>(); // Começa com memória vazia
    }

    /**
     * Limpa a memória de variáveis (opcional, para reiniciar sessão com 'clear').
     */
    public void clearVariables() {
        variables.clear();
    }

    /**
     * Método principal: Recebe um nó (raiz da sub-árvore) e retorna o resultado calculado.
     * Utiliza o recurso "Pattern Matching for instanceof" (Java 16+) para fazer o cast automático.
     * * @param node O nó da árvore a ser avaliado.
     * @return O número complexo resultante.
     */
    public ComplexNumber evaluate(ASTNode node) {
        
        // --- Caso Base 1: Nó Numérico (Folha) ---
        // Se for um número, apenas retorna o valor que ele guarda.
        if (node instanceof NumberNode numberNode) {
            return numberNode.value();
        }

        // --- Caso Base 2: Variável (Folha) ---
        // Se for variável, precisamos descobrir seu valor (Regra 7).
        if (node instanceof VariableNode variableNode) {
            return resolveVariable(variableNode.name());
        }

        // --- Caso Recursivo 1: Operação Binária (Galho) ---
        // (+, -, *, /, **)
        if (node instanceof BinaryOperationNode binaryNode) {
            // 1. Avalia o lado esquerdo recursivamente (desce na árvore)
            ComplexNumber leftValue = evaluate(binaryNode.left());
            
            // 2. Avalia o lado direito recursivamente
            ComplexNumber rightValue = evaluate(binaryNode.right());

            // 3. Aplica a operação correspondente aos valores retornados
            return switch (binaryNode.operator()) {
                case PLUS -> leftValue.sum(rightValue);
                case MINUS -> leftValue.subtract(rightValue);
                case MULTIPLY -> leftValue.multiply(rightValue);
                case DIVIDE -> leftValue.divide(rightValue); // Tratamento de erro por Divisão por Zero (div/0) é tratada dentro do método divide()
                case POWER -> leftValue.power(rightValue);
                default -> throw new RuntimeException("Operador desconhecido: " + binaryNode.operator());
            };
        }

        // --- Caso Recursivo 2: Operação Unária (Galho) ---
        // (conj) - conjugado
        if (node instanceof UnaryOperationNode unaryNode) {
            // Avalia o operando primeiro
            ComplexNumber operandValue = evaluate(unaryNode.operand());
            
            if (unaryNode.operator() == TokenType.CONJUGATE) {
                return operandValue.conjugate();
            }
            throw new RuntimeException("Função unária desconhecida: " + unaryNode.operator());
        }

        // --- Caso Recursivo 3: Raiz N-ésima (Galho Especial) ---
        // (root[n])
        if (node instanceof NthRootNode rootNode) {
            // Avalia a expressão dentro da raiz
            ComplexNumber radicandoValue = evaluate(rootNode.radicando());
            
            // Chama o método nthRoot que criamos na Fase 1
            // Acessa o grau (inteiro) diretamente do nó
            return radicandoValue.nthRoot(rootNode.degree());
        }

        throw new RuntimeException("Erro de Execução: Tipo de nó desconhecido na árvore.");
    }

    /**
     * Calculo de variáveis na expressão.
     * Lógica:
     * 1. Verifica se a variável já existe na memória.
     * 2. Se não, PEDE AO USUÁRIO o valor dela.
     * 3. Permite que o usuário digite uma expressão (ex: "2+2") como valor.
     */
    private ComplexNumber resolveVariable(String name) {
        // 1. Verifica se já sabemos o valor (Cache)
        if (variables.containsKey(name)) {
            return variables.get(name);
        }

        // 2. Se não sabemos, pergunta ao usuário (Interatividade)
        System.out.print(" >> Defina o valor para '" + name + "' (ex: 3+4i ou 5*2): ");
        String input = scanner.nextLine();

        try {
            // 2a. Analisa a entrada do usuário (Recursão indireta)
            // Usamos o nosso próprio Tokenizer e Parser para processar a resposta do usuário!
            // Isso permite que o usuário defina x como "2 + 3 * 5" se ele quiser.
            Tokenizer tempTokenizer = new Tokenizer(input);
            Parser tempParser = new Parser(tempTokenizer.tokenize());
            ASTNode tempAst = tempParser.parse();
            
            // Avalia a resposta do usuário (Recursão indireta)
            ComplexNumber value = evaluate(tempAst);
            
            // 3. Guarda na memória para não perguntar de novo
            variables.put(name, value);
            return value;

        } catch (Exception e) {
            // Tratamento de erro na entrada de dados
            System.out.println(" !! Valor inválido para '" + name + "': " + e.getMessage());
            System.out.println(" !! Tente novamente.");
            return resolveVariable(name); // Tenta de novo por chamada recursiva
        }
    }
}