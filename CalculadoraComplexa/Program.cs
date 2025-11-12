using CalculadoraComplexa;

Console.WriteLine("--- Teste da Calculadora Complexa (Fase 1) ---");
Console.WriteLine("Este programa testa a struct ComplexNumber e suas operações.");

// Criando números (Regra 0)
ComplexNumber c1 = new ComplexNumber(3, 4);  // c1 = 3 + 4i
ComplexNumber c2 = new ComplexNumber(1, -2); // c2 = 1 - 2i
ComplexNumber c3_real = new ComplexNumber(5, 0); // c3 = 5
ComplexNumber c4_imag = new ComplexNumber(0, 7); // c4 = 7i

Console.WriteLine($"\n--- Teste ToString (Regra 0) ---");
Console.WriteLine($"c1 = {c1}"); // Esperado: 3 + 4i
Console.WriteLine($"c2 = {c2}"); // Esperado: 1 - 2i
Console.WriteLine($"c3_real = {c3_real}"); // Esperado: 5
Console.WriteLine($"c4_imag = {c4_imag}"); // Esperado: 7i


// Teste de Aritmética (Regra 1)
Console.WriteLine("\n--- Teste Aritmética (Regra 1) ---");
Console.WriteLine($"({c1}) + ({c2}) = {c1 + c2}"); // Esperado: 4 + 2i
Console.WriteLine($"({c1}) - ({c2}) = {c1 - c2}"); // Esperado: 2 + 6i
Console.WriteLine($"({c1}) * ({c2}) = {c1 * c2}"); // Esperado: 11 - 2i
Console.WriteLine($"({c1}) / ({c2}) = {c1 / c2}"); // Esperado: -1 + 2i

// Teste de Funções (Regra 1)
Console.WriteLine("\n--- Teste Funções (Regra 1) ---");
Console.WriteLine($"   Conjugado de ({c1}) = {c1.Conjugate()}"); // Esperado: 3 - 4i

// Teste de Potência (Regra 1)
Console.WriteLine("\n   --- Teste Potência  ---");
// c1^2 = (3+4i)*(3+4i) = -7 + 24i
Console.WriteLine($"   ({c1})^2 = {c1.Power(2)}"); // Esperado: ~ -7 + 24i

// Teste de Raiz (Regra 1)
Console.WriteLine("\n   --- Teste Raiz  ---");
// Raiz quadrada (n=2) de c1 = (3+4i)
// (2+1i) * (2+1i) = (4-1) + (2+2)i = 3 + 4i. Correto.
Console.WriteLine($"   Raiz(2) de ({c1}) = {c1.NthRoot(2)}"); // Esperado: ~ 2 + 1i

// Teste de Erro (Regra 5)
Console.WriteLine("\n--- Teste de Erro (Regra 5) ---");
try
{
    ComplexNumber zero = new ComplexNumber(0, 0);
    Console.WriteLine($"Tentando ({c1}) / ({zero})...");
    ComplexNumber resultado = c1 / zero;
    Console.WriteLine($"Resultado: {resultado}");
}
catch (DivideByZeroException ex)
{
    Console.WriteLine($"SUCESSO: Erro de divisão por zero capturado.");
    Console.WriteLine($"Mensagem: {ex.Message}");
}
catch (Exception ex)
{
    Console.WriteLine($"FALHA: Erro inesperado capturado: {ex.Message}");
}

Console.WriteLine("\n--- Testes da Fase 1 Concluídos ---");
