package com.facsculator;

public class App {
    public static void main(String[] args) {
        System.out.println("--- Teste da Calculadora Complexa (Fase 1 - Java) ---");
        System.out.println("Este programa testa o record ComplexNumber e suas operações.");

        // Criando números (Regra 0)
        ComplexNumber c1 = new ComplexNumber(3, 4);  // c1 = 3 + 4i
        ComplexNumber c2 = new ComplexNumber(1, -2); // c2 = 1 - 2i
        ComplexNumber c3_real = new ComplexNumber(5);    // c3 = 5 (usa o construtor auxiliar)
        ComplexNumber c4_imag = new ComplexNumber(0, 7); // c4 = 7i

        System.out.println("\n--- Teste toString (Regra 0) ---");
        System.out.println("c1 = " + c1); // Esperado: 3 + 4i
        System.out.println("c2 = " + c2); // Esperado: 1 - 2i
        System.out.println("c3_real = " + c3_real); // Esperado: 5
        System.out.println("c4_imag = " + c4_imag); // Esperado: 7i


        // Teste de Aritmética (Regra 1)
        System.out.println("\n--- Teste Aritmética (Regra 1) ---");
        // Em Java, usamos os métodos: .add(), .subtract(), etc.
        System.out.println("(" + c1 + ") + (" + c2 + ") = " + c1.add(c2)); // Esperado: 4 + 2i
        System.out.println("(" + c1 + ") - (" + c2 + ") = " + c1.subtract(c2)); // Esperado: 2 + 6i
        System.out.println("(" + c1 + ") * (" + c2 + ") = " + c1.multiply(c2)); // Esperado: 11 - 2i
        System.out.println("(" + c1 + ") / (" + c2 + ") = " + c1.divide(c2)); // Esperado: -1 + 2i

        // Teste de Funções (Regra 1)
        System.out.println("\n--- Teste Funções (Regra 1) ---");
        System.out.println("Conjugado de (" + c1 + ") = " + c1.conjugate()); // Esperado: 3 - 4i
        
        // Teste de Potência (Regra 1)
        // c1^2 = (3+4i)*(3+4i) = -7 + 24i
        System.out.println("(" + c1 + ")^2 = " + c1.power(2)); // Esperado: ~ -7 + 24i
        
        // Teste de Raiz (Regra 1)
        // Raiz quadrada (n=2) de c1 = (3+4i)
        // (2+1i) * (2+1i) = (4-1) + (2+2)i = 3 + 4i. Correto.
        System.out.println("Raiz(2) de (" + c1 + ") = " + c1.nthRoot(2)); // Esperado: ~ 2 + 1i

        // Teste de Erro (Regra 5)
        System.out.println("\n--- Teste de Erro (Regra 5) ---");
        try {
            ComplexNumber zero = new ComplexNumber(0, 0);
            System.out.println("Tentando (" + c1 + ") / (" + zero + ")...");
            ComplexNumber resultado = c1.divide(zero);
            System.out.println("Resultado: " + resultado);
        } catch (ArithmeticException ex) {
            // Exceção esperada (em Java, é ArithmeticException para / por zero)
            System.out.println("SUCESSO: Erro de divisão por zero capturado.");
            System.out.println("Mensagem: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("FALHA: Erro inesperado capturado: " + ex.getMessage());
        }

        System.out.println("\n--- Testes da Fase 1 Concluídos ---");
    }
}