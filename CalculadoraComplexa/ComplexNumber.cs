namespace CalculadoraComplexa
{
    public readonly struct ComplexNumber
    {
        // Seguindo a definição básica (a + bi)
        // Lembrando que i = raiz quadrada de -1
        public double Real { get; }      // Parte real 'a'
        public double Imaginary { get; } // Parte imaginaria 'b*i'

        // Construtor
        public ComplexNumber(double real, double imaginary)
        {
            Real = real;
            Imaginary = imaginary;
        }

        // Regra 0: Método para exibir o número formatado
        public override string ToString()
        {
            // Antes de exibir, arredondamos para evitar "lixo" de ponto flutuante
            // Deliminatmos 10 casas decimais para arredondar os resultados sem perder precisão real.

            int displayPrecision = 10;
            double Real = Math.Round(this.Real, displayPrecision);
            double Imaginary = Math.Round(this.Imaginary, displayPrecision);


            // Lógica para formatação "a + bi" ou "a - bi"
            // Para evitar equivocos com ponto flutuante pegamos o valor absolut e comparamos com um valor extremamente próximo a zero

            if (Math.Abs(Imaginary) < 1e-9)
            {
                return $"{Real}";
            }

            if (Math.Abs(Real) < 1e-9)
            {
                return $"{Imaginary}i";
            }

            // Se a parte imaginária for negativa, o sinal já vem
            if (Imaginary < 0)
            {
                return $"{Real} - {Math.Abs(Imaginary)}i";
            }

            return $"{Real} + {Imaginary}i";
        }


        // --- Operações Aritiméticas ---

        /// <summary>
        /// Retorna o conjugado (a - bi).
        /// </summary>
        public ComplexNumber Conjugate()
        {
            return new ComplexNumber(Real, -Imaginary);
        }

        /// <summary>
        /// Reescreve o operador básico de adição para o equivalente em números complexos: 
        /// Adição (a+bi) + (c+di) = (a+c) + (b+d)i
        /// </summary>
        public static ComplexNumber operator +(ComplexNumber a, ComplexNumber b)
        {
            return new ComplexNumber(a.Real + b.Real, a.Imaginary + b.Imaginary);
        }

        /// <summary>
        /// Reescreve o operador básico de subtração para o equivalente em números complexos: 
        /// Subtração (a+bi) - (c+di) = (a-c) + (b-d)i
        /// </summary>
        public static ComplexNumber operator -(ComplexNumber a, ComplexNumber b)
        {
            return new ComplexNumber(a.Real - b.Real, a.Imaginary - b.Imaginary);
        }

        /// <summary>
        /// Reescreve o operador básico de multiplicação  para o equivalente em números complexos: 
        /// Multiplicação (a+bi) * (c+di) = (ac-bd) + (ad+bc)i
        /// </summary>
        public static ComplexNumber operator *(ComplexNumber a, ComplexNumber b)
        {
            double newReal = a.Real * b.Real - a.Imaginary * b.Imaginary;
            double newImag = a.Real * b.Imaginary + a.Imaginary * b.Real;
            return new ComplexNumber(newReal, newImag);
        }

        /// <summary>
        /// Reescreve o operador básico de divisão para o equivalente em números complexos: 
        /// Divisão (a+bi) / (c+di)
        /// </summary>
        public static ComplexNumber operator /(ComplexNumber a, ComplexNumber b)
        {
            // Denominador = c^2 + d^2
            double denominator = b.Real * b.Real + b.Imaginary * b.Imaginary;

            // Regra 5: Detectar divisão por zero
            if (Math.Abs(denominator) < 1e-9)
            {
                throw new DivideByZeroException("Divisão por número complexo zero (0+0i).");
            }

            // Numerador = (a+bi) * (c-di) -> a * b.Conjugate()
            ComplexNumber numerator = a * b.Conjugate();

            return new ComplexNumber(numerator.Real / denominator, numerator.Imaginary / denominator);
        }

        // --- Funções Polares (Auxiliares para Potência/Raiz) ---

        /// <summary>
        /// Calcula a Magnitude (Raio 'r', ou |z|) do número complexo.
        /// Corresponde à distância Euclidiana do ponto (a, b) até a origem (0, 0)
        /// no plano complexo, com base no Teorema de Pitágoras.
        /// Fórmula: r = sqrt(a^2 + b^2)
        /// </summary>
        public double Magnitude
        {
            get
            {
                // Aplica o Teorema de Pitágoras
                return Math.Sqrt(Real * Real + Imaginary * Imaginary);
            }
        }

        /// <summary>
        /// Calcula a Fase (Ângulo 'θ') do número complexo em radianos.
        /// Corresponde ao ângulo que o vetor (a, b) faz com o eixo real positivo.
        /// Utiliza Math.Atan2(b, a) em vez de Math.Atan(b/a) para
        /// garantir que o ângulo esteja no quadrante correto (0 a 2π ou -π a π).
        /// </summary>
        public double Phase
        {
            get
            {
                // Atan2 lida com todos os quadrantes e evita divisão por zero se Real=0
                return Math.Atan2(Imaginary, Real);
            }
        }

        // --- Potência e Raiz (Regra 1) ---

        /// <summary>
        /// Calcula a potência de um número complexo elevado a um expoente real.
        /// Utiliza a Fórmula de De Moivre, que requer a conversão para
        /// coordenadas polares (Magnitude 'r', Fase 'θ').
        /// Fórmula: z^n = r^n * (cos(nθ) + i*sin(nθ))
        /// </summary>
        /// <param name="exponent">O expoente (n) ao qual elevar o número.</param>
        /// <returns>Um novo ComplexNumber com o resultado de z^n.</returns>
        public ComplexNumber Power(double exponent)
        {
            // --- Passo 1: Calcular os novos componentes polares (r', θ') ---

            // 1a. Calcula a nova magnitude (r'): r^n
            // (onde 'Magnitude' é 'r' e 'exponent' é 'n')
            double newMagnitude = Math.Pow(Magnitude, exponent);

            // 1b. Calcula a nova fase (θ'): n * θ
            // (onde 'Phase' é 'θ' e 'exponent' é 'n')
            double newPhase = Phase * exponent;

            // --- Passo 2: Converter os novos componentes polares de volta para retangulares (a, b) ---

            // 2a. Calcula a nova parte real: a = r' * cos(θ')
            double newReal = newMagnitude * Math.Cos(newPhase);

            // 2b. Calcula a nova parte imaginária: b = r' * sin(θ')
            double newImag = newMagnitude * Math.Sin(newPhase);

            // Retorna o novo número complexo a partir dos componentes retangulares calculados
            return new ComplexNumber(newReal, newImag);
        }

        /// <summary>
        /// Calcula a n-ésima raiz principal de um número complexo.
        /// Matematicamente, isso é equivalente a calcular z^(1/n).
        /// Esta função reutiliza o método Power para maior consistência.
        /// </summary>
        /// <param name="n">O índice da raiz (ex: 2 para raiz quadrada, 3 para cúbica).</param>
        /// <returns>Um novo ComplexNumber com o resultado da raiz principal.</returns>
        /// <exception cref="ArgumentException">Lançada se n for menor ou igual a zero,
        /// pois índices de raiz não-positivos não são definidos neste contexto.</exception>
        public ComplexNumber NthRoot(int n)
        {
            // Regra 5: Validação da entrada. 
            // Índices de raiz devem ser inteiros positivos.
            if (n <= 0)
            {
                // Rejeita a expressão/operação inválida.
                throw new ArgumentException("O índice da raiz (n) deve ser um inteiro positivo maior que zero.");
            }

            // A n-ésima raiz é matematicamente idêntica à potência de (1/n).
            // Ex: Raiz Quadrada (n=2) é o mesmo que Power(1.0 / 2) ou Power(0.5).
            double exponent = 1.0 / n;

            // Reutiliza a lógica de potência já implementada.
            return Power(exponent);
        }

    }
}
