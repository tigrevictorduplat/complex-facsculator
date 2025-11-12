using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

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
    }
}
