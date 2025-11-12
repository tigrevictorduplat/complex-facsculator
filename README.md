#  Calculadora Científica de Números Complexos (A3)

Este repositório contém o código-fonte do Trabalho A3: uma calculadora científica de números complexos em **Java**, capaz de analisar e executar expressões matemáticas.

O projeto segue estritamente as regras definidas na especificação do trabalho, incluindo a análise de expressões com árvores (AST) e a exibição da árvore em notação LISP.

## 👥 Integrantes

* **Víctor Duplat Tigre**
* **Jorge Eduardo**

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java (JDK 17)
* **Build:** Maven
* **Ambiente:** Docker 

---

## 🚀 Como Executar (via Docker)

O projeto é "containerizado" para garantir que ele funcione em qualquer máquina que tenha o Docker instalado, sem a necessidade de configurar o ambiente Java/Maven localmente.

### 1. Pré-requisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (ou Docker Engine no Linux).

### 2. Construir (Build) a Imagem

1.  **Abra seu terminal** na raiz do repositório (ex: `complex-facsculator`).

2.  **Navegue para dentro** da pasta do projeto Java (onde está o `pom.xml`):

    ```bash
    cd complex-facsculator
    ```

3.  **Execute o build** na pasta do projeto:

    ```bash
    docker build -t complex-facsculator .
    ```

### 3. Executar (Run) o Container

```bash
docker run -it --rm complex-facsculator
```

-----

## 🧬 Arquitetura do Código

Esta seção documenta os principais módulos do sistema.

### (`ComplexNumber.java`)

  * **Representação:** A classe foi implementada como um `record` do Java (Java 16+). Isso garante **imutabilidade** (um número complexo, uma vez criado, não pode ser alterado) e fornece automaticamente construtores, acesso, `equals()` e `hashCode()`.
  * **Atributos:** `real()` (double) e `imaginary()` (double), acessados como métodos.
  * **Aritmética:** Como o Java não possui sobrecarga de operadores, a aritmética básica (`+`, `-`, `*`, `/`) foi implementada usando **métodos explícitos**: `.add()`, `.subtract()`, `.multiply()`, `.divide()`.
  * **Funções Avançadas:**
      * `conjugate()`: Retorna o conjugado.
      * `power(n)`: Implementa a potenciação usando a **Fórmula de De Moivre**, convertendo o número para coordenadas polares (Magnitude e Fase).
      * `nthRoot(n)`: Implementa a radiciação (raiz principal) através da `power(1.0 / n)`.
  * **Exibição:** O método `toString()` foi sobrescrito para formatar a saída de forma limpa, tratando casos especiais como números reais puros (`5`), imaginários puros (`7i` ou `i`) e formatação de sinal (`3 - 4i` ou `3 - i`).
  * **Segurança:** O método `.divide()` detecta ativamente a divisão por zero (denominador \< 1e-9) e lança uma `ArithmeticException` customizada.

-----

### 🧬 Explicação Lógica e Matemática (`power`) e Raiz (`nthRoot`)

Para implementar as operações potência `**` e raiz, não podemos simplesmente multiplicar o número `n` vezes. Isso só funcionaria para inteiros positivos e falharia para `(3+4i)^0.5` (que é uma raiz) ou `(3+4i)^-2`.

A solução matemática universal é usar a **Forma Polar** de um número complexo, que leva à poderosa **Fórmula de De Moivre**.

#### Coordenadas Retangulares vs Coordenadas Polares

Normalmente, pensamos em um número complexo `z = a + bi` em um gráfico com um eixo Real ('a') e um eixo Imaginário ('b'). Essas são as chamadas **coordenadas retangulares**. Que são ótimas para somar e subtrair, mas péssimas para multiplicação e potenciação.

Mas conseguimos representar **exatamente o mesmo ponto** no gráfico usando duas outras informações:

1.  **Raio (r):** A distância da origem (0,0) até o ponto. (No código: `magnitude()`)
2.  **Ângulo (θ):** O ângulo que a linha da origem até o ponto faz com o eixo real. (No código: `phase()`)

Veja como multiplicar em forma polar é fácil:

  * **Para multiplicar `z1` e `z2`:** Multiplique suas magnitudes ($r_1 \cdot r_2$) e **some** seus ângulos ($\theta_1 + \theta_2$).

Com esse método tornamos a potenciação muito mais simples.

-----

### 1\. Os Métodos Auxiliares (Conversão para Polar)

Para usar a forma polar, primeiro precisamos ser capazes de encontrá-la. Criamos dois métodos auxiliares no `record`: `magnitude()` e `phase()`.

#### `magnitude()` (O Raio $r$)

  * **Matemática:** A magnitude $r$ (ou $|z|$) é a distância do ponto `(a, b)` até a origem. Usamos o **Teorema de Pitágoras**.
    $$r = \sqrt{a^2 + b^2}$$
  * **Lógica (Código):** A implementação é uma tradução direta da fórmula matemática.
    ```java
    public double magnitude() {
        return Math.sqrt(this.real * this.real + this.imaginary * this.imaginary);
    }
    ```

#### `phase()` (O Ângulo $\theta$)

  * **Matemática:** A fase $\theta$ é o ângulo encontrado usando trigonometria. A função básica é a arco-tangente: $\theta = \arctan(b/a)$.
  * **Lógica (Código):** usamos `Math.atan2(b, a)`. uma função especial feita exatamente para isso: ela recebe *ambos* os componentes `b` e `a` e usa seus sinais para descobrir corretamente o ângulo em todos os quatro quadrantes do plano cartesiano.
    ```java
    public double phase() {
        return Math.atan2(this.imaginary, this.real);
    }
    ```

-----

### 2\. O Método `power(double exponent)`

Este método calcula $z^n$, onde `n` pode ser qualquer número real (`double`).

  * **Matemática (Fórmula de De Moivre):**
    Para calcular $z^n$, a fórmula é:

    1.  Eleve a magnitude à potência $n$: $r^n$
    2.  Multiplique a fase por $n$: $n \cdot \theta$

    Isso nos dá um novo ponto polar $(r^n, n\theta)$. A fórmula completa é:
    $$z^n = r^n \cdot (\cos(n\theta) + i \cdot \sin(n\theta))$$

  * **Lógica (Código):** O código segue a fórmula passo a passo:

    ```java
    public ComplexNumber power(double exponent) {
        // 1. Calcular a nova magnitude: r^n
        double newMagnitude = Math.pow(this.magnitude(), exponent);

        // 2. Calcular a nova fase: n * θ
        double newPhase = this.phase() * exponent;

        // 3. Converter de volta para retangular (a, b)
        // a = r * cos(θ)
        double newReal = newMagnitude * Math.cos(newPhase);
        // b = r * sin(θ)
        double newImag = newMagnitude * Math.sin(newPhase);

        // 4. Retornar o novo número complexo
        return new ComplexNumber(newReal, newImag);
    }
    ```

-----

### 3\. O Método `nthRoot(int n)`

Este método calcula a $\sqrt[n]{z}$ (raiz n-ésima de $z$).

  * **Matemática:** A raiz nada mais é do que uma **potência fracionária**.
    $$\sqrt[n]{z} = z^{(1/n)}$$
    (Por exemplo, a raiz quadrada $\sqrt{z}$ é o mesmo que $z^{0.5}$).

  * **Lógica (Código):** Graças à forma como fizemos o `power` (aceitando um `double`), este método se torna incrivelmente simples e elegante. Nós apenas reutilizamos o `power` com o expoente `1.0 / n`.

    ```java
    public ComplexNumber nthRoot(int n) {
        // Regra 5: Proteção contra entrada inválida
        if (n <= 0) {
            throw new IllegalArgumentException("A raiz (n) deve ser um inteiro positivo.");
        }
        
        // Reutiliza o power com o expoente fracionário
        return power(1.0 / n);
    }
    ```

*(OBS: Matematicamente, existem 'n' raízes para qualquer $\sqrt[n]{z}$. Esta função retorna a **raiz principal**, que é o padrão para calculadoras científicas).*
