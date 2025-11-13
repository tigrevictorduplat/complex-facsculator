// src/main/java/com/facsculator/ComplexNumber.java
package com.facsculator;

// imports
import java.text.DecimalFormat; // Para formatação numérica
import static java.lang.Math.PI; // Importa o PI estaticamente

/**
 * Representa um número complexo (a + bi) de forma imutável.
 * * Implementado como um 'record' Java (Java 16+) para garantir imutabilidade
 * e fornecer automaticamente construtores, acesso, equals() e hashCode().
 */
public record ComplexNumber(double real, double imaginary) {

    // --- Construtores Auxiliares ---

    /**
     * Construtor auxiliar para criar um número real puro (b=0).
     * 
     * @param real A parte real (a).
     */
    public ComplexNumber(double real) {
        this(real, 0); // Chama o construtor principal (real, 0)
    }

    /**
     * Formatador para "limpar" a exibição de doubles.
     * "0.##############" remove zeros à direita (1.0 -> "1")
     * mas preserva decimais significativos (1.000005 -> "1.000005").
     */
    private static final DecimalFormat df = new DecimalFormat("0.##############");

    // --- Exibição ---

    /**
     * Converte o número para uma string formatada (ex: "3 - 4i", "5", "7i").
     * Sobrescreve o método toString() padrão do 'record' para formatação limpa,
     * tratando casos especiais e removendo zeros decimais não significativos.
     *
     * @return Uma representação em string limpa e formatada do número complexo.
     */
    @Override
    public String toString() {
        // Pré-arredonda para 10 casas decimais para limpar "lixo" E-16
        // antes de passar para o formatador.
        int displayPrecision = 10;
        double r = Math.round(this.real * Math.pow(10, displayPrecision)) / Math.pow(10, displayPrecision);
        double i = Math.round(this.imaginary * Math.pow(10, displayPrecision)) / Math.pow(10, displayPrecision);

        // Formata os números arredondados usando nosso formatador "limpo"
        String rStr = df.format(r);
        String iAbsStr = df.format(Math.abs(i));

        // --- Lógica de Formatação ---

        // Caso 1: Número é (praticamente) real (ex: 5)
        if (Math.abs(i) < 1e-9) {
            return rStr; // Ex: "5", "1.000005", "0"
        }

        // Caso 2: Número é (praticamente) imaginário puro (ex: 7i)
        if (Math.abs(r) < 1e-9) {
            // Se i=1, retorna "i". Se i=-1, retorna "-i"
            if (iAbsStr.equals("1")) {
                return (i < 0) ? "-i" : "i";
            }
            // Senão, retorna o número formatado
            return df.format(i) + "i"; // Ex: "7i", "-2.5i", "0.000005i"
        }

        // Caso 3: Número complexo com parte imaginária negativa (ex: 3 - 4i)
        if (i < 0) {
            // Se i=-1, retorna "3 - i"
            if (iAbsStr.equals("1")) {
                return rStr + " - i";
            }
            return rStr + " - " + iAbsStr + "i"; // Ex: "3 - 4i", "1.5 - 0.000005i"
        }

        // Caso 4: Padrão (ex: 3 + 4i)
        // Se i=1, retorna "3 + i"
        if (iAbsStr.equals("1")) {
            return rStr + " + i";
        }
        return rStr + " + " + iAbsStr + "i"; // Ex: "3 + 4i", "1.5 + 0.000005i"
    }
    // --- Operações Aritméticas ---

    /**
     * Adição (a+bi) + (c+di) = (a+c) + (b+d)i
     * 
     * @param other O outro número complexo (c+di) a ser somado.
     * @return Um novo ComplexNumber com o resultado da soma.
     */
    public ComplexNumber sum(ComplexNumber other) {
        return new ComplexNumber(this.real + other.real, this.imaginary + other.imaginary);
    }

    /**
     * Subtração (a+bi) - (c+di) = (a-c) + (b-d)i
     * 
     * @param other O outro número complexo (c+di) a ser subtraído.
     * @return Um novo ComplexNumber com o resultado da subtração.
     */
    public ComplexNumber subtract(ComplexNumber other) {
        return new ComplexNumber(this.real - other.real, this.imaginary - other.imaginary);
    }

    /**
     * Multiplicação (a+bi) * (c+di) = (ac-bd) + (ad+bc)i
     * 
     * @param other O outro número complexo (c+di) a ser multiplicado.
     * @return Um novo ComplexNumber com o resultado da multiplicação.
     */
    public ComplexNumber multiply(ComplexNumber other) {
        double newReal = this.real * other.real - this.imaginary * other.imaginary;
        double newImag = this.real * other.imaginary + this.imaginary * other.real;
        return new ComplexNumber(newReal, newImag);
    }

    /**
     * Retorna o conjugado (a - bi).
     * 
     * @return Um novo ComplexNumber com a parte imaginária invertida.
     */
    public ComplexNumber conjugate() {
        return new ComplexNumber(this.real, -this.imaginary);
    }

    /**
     * Divisão (a+bi) / (c+di).
     * Implementado multiplicando pelo conjugado: [(a+bi)*(c-di)] / (c^2 + d^2)
     * 
     * @param other O divisor (c+di).
     * @return Um novo ComplexNumber com o resultado da divisão.
     * @throws ArithmeticException se o divisor for zero (0+0i).
     */
    public ComplexNumber divide(ComplexNumber other) {
        // Denominador = c^2 + d^2
        double denominator = other.real * other.real + other.imaginary * other.imaginary;

        // Detectar divisão por zero
        if (Math.abs(denominator) < 1e-9) {
            throw new ArithmeticException("Divisão por número complexo zero (0+0i).");
        }

        // Numerador = (a+bi) * (c-di) -> this * other.conjugate()
        ComplexNumber numerator = this.multiply(other.conjugate());

        return new ComplexNumber(numerator.real / denominator, numerator.imaginary / denominator);
    }

    // --- Funções Polares (Auxiliares para Potência/Raiz) ---

    /**
     * Calcula a Magnitude (Raio 'r', ou |z|) do número complexo.
     * Corresponde à distância Euclidiana do ponto (a, b) até a origem (0, 0)
     * no plano complexo, com base no Teorema de Pitágoras.
     * Fórmula: r = sqrt(a^2 + b^2)
     * 
     * @return A magnitude (um double >= 0).
     */
    public double magnitude() {
        // Aplica o Teorema de Pitágoras
        return Math.sqrt(this.real * this.real + this.imaginary * this.imaginary);
    }

    /**
     * Calcula a Fase (Ângulo 'θ') do número complexo em radianos.
     * Corresponde ao ângulo que o vetor (a, b) faz com o eixo real positivo.
     * Utiliza Math.atan2(b, a) para garantir que o ângulo esteja
     * no quadrante correto.
     * 
     * @return A fase (um double em radianos, geralmente de -PI a PI).
     */
    public double phase() {
        // Atan2 lida com todos os quadrantes e evita divisão por zero se real=0
        return Math.atan2(this.imaginary, this.real);
    }

    // --- Potência e Raiz (Regra 1) ---

    /**
     * Calcula a potência de um número complexo elevado a um expoente real.
     * Utiliza a Fórmula de De Moivre, que requer a conversão para
     * coordenadas polares (Magnitude 'r', Fase 'θ').
     * Fórmula: z^n = r^n * (cos(nθ) + i*sin(nθ))
     * 
     * @param exponent O expoente (n) ao qual elevar o número.
     * 
     * @return Um novo ComplexNumber com o resultado de z^n.
     */
    public ComplexNumber power(double exponent) {
        // --- Passo 1: Calcular os novos componentes polares (r', θ') ---

        // 1a. Calcula a nova magnitude (r'): r^n
        double newMagnitude = Math.pow(this.magnitude(), exponent);

        // 1b. Calcula a nova fase (θ'): n * θ
        double newPhase = this.phase() * exponent;

        // --- Passo 2: Converter os novos componentes polares de volta para
        // retangulares (a, b) ---

        // 2a. Calcula a nova parte real: a = r' * cos(θ')
        double newReal = newMagnitude * Math.cos(newPhase);

        // 2b. Calcula a nova parte imaginária: b = r' * sin(θ')
        double newImag = newMagnitude * Math.sin(newPhase);

        // Retorna o novo número complexo a partir dos componentes retangulares
        // calculados
        return new ComplexNumber(newReal, newImag);
    }

    /**
     * Calcula a n-ésima raiz principal de um número complexo.
     * Matematicamente, isso é equivalente a calcular z^(1/n).
     * Esta função reutiliza o método power() para maior consistência.
     * * @param n O índice da raiz (ex: 2 para raiz quadrada, 3 para cúbica).
     * 
     * @return Um novo ComplexNumber com o resultado da raiz principal.
     * @throws IllegalArgumentException (Regra 5) se n for menor ou igual a zero.
     */
    public ComplexNumber nthRoot(int n) {
        // Validação da entrada.
        // Índices de raiz devem ser inteiros positivos.
        if (n <= 0) {
            // Rejeita a expressão/operação inválida.
            throw new IllegalArgumentException("O índice da raiz (n) deve ser um inteiro positivo maior que zero.");
        }

        // A n-ésima raiz é matematicamente idêntica à potência de (1/n).
        double exponent = 1.0 / n;

        // Reutiliza a lógica de potência já implementada.
        return power(exponent);
    }
}