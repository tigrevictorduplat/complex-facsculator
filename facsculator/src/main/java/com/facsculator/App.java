package com.facsculator;
import java.util.Scanner;

/**
 * Interface de Linha de Comando (CLI).
 * Implementa o loop principal e os comandos do sistema.
 * Atende às Regras 3, 6 e 10.
 */
public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Evaluator evaluator = new Evaluator(scanner);

        System.out.println("""
         _______    ___       ______     _______.  ______  __    __   __          ___   .___________.  ______   .______      \r
        |   ____|  /   \\     /      |   /       | /      ||  |  |  | |  |        /   \\  |           | /  __  \\  |   _  \\     \r
        |  |__    /  ^  \\   |  ,----'  |   (----`|  ,----'|  |  |  | |  |       /  ^  \\ `---|  |----`|  |  |  | |  |_)  |    \r
        |   __|  /  /_\\  \\  |  |        \\   \\    |  |     |  |  |  | |  |      /  /_\\  \\    |  |     |  |  |  | |      /     \r
        |  |    /  _____  \\ |  `----.----)   |   |  `----.|  `--'  | |  `----./  _____  \\   |  |     |  `--'  | |  |\\  \\----.\r
        |__|   /__/     \\__\\ \\______|_______/     \\______| \\______/  |_______/__/     \\__\\  |__|      \\______/  | _| `._____|\r
                                                                                                                            \s""");
        showHelpMenu();
        // Loop Principal (Regra 10)
        while (true) {
            System.out.print("CALC> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) continue;

            // --- Comandos de Controle ---
            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Encerrando... Obrigado por Facscular conosco!");
                break;
            }

            if (line.equalsIgnoreCase("help") || line.equalsIgnoreCase("demo")) {
                showHelp();
                continue;
            }
            if (line.equalsIgnoreCase("menu")) {
                showHelpMenu();
                continue;
            }

            if (line.equalsIgnoreCase("clear")) {
                evaluator.clearVariables();
                System.out.println(" [Memória de variáveis limpa]");
                continue;
            }

            try {
                // --- Comando TREE ---
                if (line.startsWith("tree ")) {
                    String expr = line.substring(5); // Remove "tree "
                    ASTNode ast = parseExpression(expr);
                    System.out.println(" => Árvore LISP: " + ast.toLispString());
                }
                
                // --- Comando CHECK ---
                else if (line.startsWith("check ")) {
                    handleCheckCommand(line, evaluator);
                }
                
                // --- Execução Padrão ---
                else {
                    ASTNode ast = parseExpression(line);
                    ComplexNumber result = evaluator.evaluate(ast);
                    System.out.println(" => Resultado: " + result);
                }

                

            } catch (Exception e) {
                // Tratamento de erros de execução e sintaxe
                System.out.println(" ERRO: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
    }
/**
     * Exibe o menu de comandos disponíveis.
     */
private static void showHelpMenu() {
        System.out.println("=========================================================================================================================");
        System.out.println("Calculadora Científica de Números Complexos");
        System.out.println(" Comandos:");
        System.out.println("  - Digite uma expressão para calcular (ex: (3+2i) * x)");
        System.out.println("  - 'tree <expressao>' : Mostra a árvore LISP ");
        System.out.println("  - 'check <expr1> == <expr2>' : Verifica igualdade entre duas expressões");
        System.out.println("  - 'clear' : Limpa as variáveis da memória");
        System.out.println("  - 'hekp' : Mostra exemplos de uso da calculadora");
        System.out.println("  - 'menu' : Mostra este menu de comandos novamente");
        System.out.println("  - 'exit' : Sair");
        System.out.println("=========================================\n");

    }

    /**
     * Exibe um guia rápido de uso e exemplos.
     */
    private static void showHelp() {
        System.out.println("\n=== GUIA RÁPIDO & DEMONSTRAÇÃO ===");
        System.out.println("Aqui estão exemplos do que você pode digitar:\n");

        System.out.println("1. Aritmética Básica:");
        System.out.println("   > 3 + 4i            (Soma simples)");
        System.out.println("   > (1+i) * (1-i)     (Multiplicação conjugada)");
        System.out.println("   > 10 / 2i           (Divisão por imaginário)");

        System.out.println("\n2. Potência e Raiz:");
        System.out.println("   > (2+3i) ** 2       (Quadrado de um complexo)");
        System.out.println("   > root[2](-4)       (Raiz quadrada de negativo)");
        System.out.println("   > root[3](8i)       (Raiz cúbica)");

        System.out.println("\n3. Funções e Variáveis:");
        System.out.println("   > conj(3+4i)        (Calcula o conjugado)");
        System.out.println("   > x * (2+i)         (Usa variável 'x' - pedirá o valor se não existir)");
        System.out.println("   > check a == b      (Verifica se a expressão 'a' é igual a 'b')");

        System.out.println("\n4. Visualização:");
        System.out.println("   > tree 2 * (x + 1)  (Mostra a estrutura da árvore sintática)");
        System.out.println("==================================\n");
    }

    /**
     * Helper para analisar a string "check A == B".
     */
    private static void handleCheckCommand(String line, Evaluator evaluator) throws Exception {
        // Remove "check "
        String content = line.substring(6); 
        
        // Separa as duas expressões pelo "=="
        String[] parts = content.split("==");
        if (parts.length != 2) {
            throw new Exception("Formato inválido. Use: check <expr1> == <expr2>");
        }

        System.out.println(" ... Calculando expressão 1 ...");
        ASTNode ast1 = parseExpression(parts[0]);
        ComplexNumber res1 = evaluator.evaluate(ast1);

        System.out.println(" ... Calculando expressão 2 ...");
        ASTNode ast2 = parseExpression(parts[1]);
        ComplexNumber res2 = evaluator.evaluate(ast2);

        // Verifica a igualdade com tolerância
        // (Como são doubles, usamos uma margem de erro pequena)
        double diffReal = Math.abs(res1.real() - res2.real());
        double diffImag = Math.abs(res1.imaginary() - res2.imaginary());
        boolean areEqual = diffReal < 1e-9 && diffImag < 1e-9;

        System.out.println(" => Resultado 1: " + res1);
        System.out.println(" => Resultado 2: " + res2);
        
        if (areEqual) {
            System.out.println(" AS EXPRESSÕES SÃO IGUAIS.");
        } else {
            System.out.println("AS EXPRESSÕES SÃO DIFERENTES.");
        }
    }

    /**
     * Helper centralizado para chamar o Tokenizer e Parser.
     */
    private static ASTNode parseExpression(String input) throws Exception {
        Tokenizer tokenizer = new Tokenizer(input);
        Parser parser = new Parser(tokenizer.tokenize());
        return parser.parse();
    }
}
