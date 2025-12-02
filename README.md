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

## 🧠 O Processo de Tokenização

Para que a calculadora entenda uma expressão como `(3+4i) * x`, devemos quebrar a string da expressão recebida  em "peças" lógicas, chamadas **Tokens**. Este processo é o que chamamos de **Análise Léxica**, e o `Tokenizer.java` é a classe que faz esse trabalho.

Esta processo de Tokenização introduz três novos componentes no projeto.

### `TokenType.java` - Classificação de Tokens

Esta classe simples é um `enum` que define todas as "categorias" possíveis para um token. Ele nos permite classificar cada "peça" da expressão no seguintes "grupos":

* **Valores:** `COMPLEX_NUMBER`, `VARIABLE`
* **Operadores:** `PLUS`, `MINUS`, `MULTIPLY`, `DIVIDE`, `POWER`
* **Funções:** `CONJUGATE`, `ROOT`
* **Símbolos:** `LEFT_PAREN` ( `(` ), `RIGHT_PAREN` ( `)` ), `LEFT_BRACKET` ( `[` ), `RIGHT_BRACKET` ( `]` )
* **Controle:** `END_OF_FILE` (para marcar o fim da expressão)

### `Token.java`

Este é um `record` simples que age como um contêiner. Ele "armazena" a informação de cada token. Cada `Token` gerado pelo `Tokenizer` possui:

1.  Um `TokenType` (o tipo, vindo do `enum`).
2.  Um `String text` (o valor original do texto, ex: `"3+4i"`, `"x"`, `"*"`).

Ao final do processo, o `Tokenizer` gera um `List<Token>`.

### `Tokenizer.java`

Esta é a classe principal desta fase. Ela implementa um **Lexer Inteligente** (*Smart Lexer*).

* **Objetivo:** Consumir a string de expressão caractere por caractere (usando `position`) e produzir a `List<Token>`.
* **Métodos Auxiliares:** Utiliza métodos de apoio cruciais para "olhar" a string:
    * `peek()`: "Espia" o caractere atual sem consumí-lo.
    * `advance()`: Consome o caractere atual e avança o ponteiro.
    * `peekNext()`: "Espia" o próximo caractere, essencial para tokens de 2 símbolos (como `**`).
* **Scanners:** Possui "scanners" dedicados para agrupar tokens complexos:
    * `scanIdentifier()`: Lê uma palavra e decide se é uma `VARIABLE` (como `x`), uma `FUNCTION` (como `conj`), ou o número `i`.
    * `scanNumber()`: O método mais complexo. É projetado para consumir um número complexo inteiro (ex: `5.5-2i`, `-i`, `+3.1`) como um único token `COMPLEX_NUMBER`.

#### A Lógica de Ambiguidade ( `+` e `-` )

A lógica mais refinada do `Tokenizer` é sua capacidade de resolver a **ambiguidade** dos símbolos `+` e `-`. Ele precisa saber a diferença entre:

* **Operador Binário:** `5 - 3` (o `-` é uma subtração).
* **Sinal Unário:** `(-3)` (o `-` é parte do número).

Ele faz isso usando uma lógica de **Contexto + Confirmação**:

1.  **Contexto (`isSign`):** Ele olha para o *token anterior*. Um sinal unário é esperado no **início** da expressão, ou após um **`(`** ou outro **operador** (ex: `5 * -3`).
2.  **Confirmação (`peekNext()`):** Ele olha para o *próximo caractere*. Um sinal unário deve ser seguido por um **dígito** ou um **`i`** (ex: `-5`, `-i`).

Se ambas as condições são verdadeiras, ele chama o `scanNumber()`. Caso contrário, ele cria um token `PLUS` ou `MINUS` (operador).

#### Detecção de Erros

Se o `Tokenizer` encontra um caractere que não reconhece (como `@` ou `#`), ele lança uma `Exception`, rejeitando a expressão por segurança. 


## 🌳 A Estrutura da Árvore (AST)

Após a tokenização, temos uma lista plana de símbolos. Para dar sentido matemático e respeitar a ordem das operações (ex: multiplicação antes da soma), organizamos esses tokens em uma **Árvore de Sintaxe Abstrata** (AST - *Abstract Syntax Tree*).

Utilizamos o **Padrão de Projeto Composite**, onde todos os elementos da árvore (sejam números simples ou expressões complexas) são tratados como um `ASTNode`.

### A Interface `ASTNode`

A interface define o contrato para qualquer nó da árvore.
* **Notação LISP:** A interface obriga que todo nó implemente o método `toLispString()`. Isso garante que a árvore possa se auto-descrever textualmente no formato LISP (ex: `(+ 3 (* 2 x))`).

### 🍃 Nós Folha (Valores)
São os nós que ficam nas pontas da árvore e não possuem filhos.
* **`NumberNode`:** Armazena um objeto `ComplexNumber` já resolvido.
    * *LISP:* Retorna o próprio número (ex: `3 + 4i`).
* **`VariableNode`:** Armazena o nome de uma variável (ex: `"x"`). Seu valor só será descoberto na fase de execução.
    * *LISP:* Retorna o nome da variável (ex: `x`).

### 🪵 Nós Galho (Operações)
São nós compostos que conectam outros nós.
* **`BinaryOperationNode`:** Representa operações matemáticas padrão (Soma, Subtração, Multiplicação, Divisão, Potência). Contém um nó à esquerda (`left`), um operador e um nó à direita (`right`).
    * *LISP:* `(OPERADOR ESQ DIR)` -> Ex: `(+ 3 5)`.
* **`UnaryOperationNode`:** Representa operações sobre um único valor, como a função conjugado `conj(z)`.
    * *LISP:* `(FUNC OPERANDO)` -> Ex: `(conj 3+4i)`.

### √ Nó de Radiciação: `NthRootNode`

Representa a operação matemática de radiciação (Raiz N-ésima). Diferente de uma operação binária comum, a raiz possui um parâmetro fixo inteiro (o grau da raiz).

* **Estrutura:** `root[grau](radicando)`
* **Por que um nó separado?** Isso garante a **tipagem forte** do grau da raiz (exigindo que seja um `int`) e facilita a validação da **Tratamento de Erro -** (evitando graus inválidos como zero ou números complexos no índice da raiz).
* *LISP:* `(root[grau] radicando)` -> Ex: `(root[3] x)`.

## 🏗️ Análise Sintática - Parser.java

Enquanto o Lexer lida com palavras isoladas, o **Parser** lida com a gramática e o significado. Ele recebe a lista linear de Tokens e a transforma em uma estrutura hierárquica: a **Árvore de Sintaxe Abstrata (AST)**.

Utilizamos o algoritmo **Recursive Descent Parser** (Descida Recursiva). A ideia central é que cada nível de precedência matemática (soma, multiplicação, parênteses) possui seu próprio método. Um método de "nível baixo" sempre chama o de "nível alto" primeiro, garantindo que operações prioritárias fiquem mais profundas na árvore (e sejam executadas antes).

### A Hierarquia de Métodos (Gramática)

Abaixo, descrevemos os métodos principais na ordem de chamada (do menor para a maior precedência).

#### 1. `parse()`
* **Função:** É o ponto de entrada. Inicia a análise chamando `expression()`.
* **Validação:** Após montar a árvore, ele verifica se o token atual é do tipo `EOF` (Fim de Arquivo). Se houver tokens sobrando (ex: `(2+2) 5` - o `5` sobra), ele lança uma exceção de sintaxe, rejeitando a expressão.

#### 2. `expression()` (Soma e Subtração)
* **Precedência:** Baixa.
* **Lógica:**
    1.  Chama `term()` para processar o lado esquerdo (garantindo que multiplicações ocorram antes).
    2.  Entra em um loop `while`: enquanto encontrar tokens `+` ou `-`, ele consome o operador, chama `term()` novamente para o lado direito e cria um novo `BinaryOpNode` combinando os dois.
* **Retorno:** Um nó representando somas/subtrações ou o resultado de `term()`.

#### 3. `term()` (Multiplicação e Divisão)
* **Precedência:** Média.
* **Lógica:** Similar ao `expression`, mas procura por `*` ou `/`. Chama `power()` para obter seus operandos.
* **Retorno:** Um nó representando multiplicações/divisões ou o resultado de `power()`.

#### 4. `power()` (Potenciação)
* **Precedência:** Alta.
* **Lógica:** Procura pelo token `**`. Chama `unary()` para obter os operandos.
* **Retorno:** Um nó `BinaryOpNode` do tipo POWER ou o resultado de `unary()`.

#### 5. `unary()` (Funções e Raízes)
* **Precedência:** Altíssima.
* **Lógica:** Verifica se o token atual é uma palavra-chave de função.
    * **`conj`:** Consome obrigatoriamente `(` + expressão + `)` e retorna um `UnaryOpNode`.
    * **`root`:** Consome obrigatoriamente `[` + grau (inteiro) + `]` + `(` + expressão + `)` e retorna um `NthRootNode`.
* **Retorno:** O nó da função ou delega para `primary()`.

#### 6. `primary()`
* **Precedência:** Máxima (O fim da recursão).
* **Lógica:** Trata os elementos base:
    * **Números:** Converte o texto em `ComplexNumber` e retorna um `NumberNode`.
    * **Variáveis:** Retorna um `VariableNode`.
    * **Parênteses `(...)`:** Se encontrar um `(`, ele chama `expression()` **recursivamente**. Isso reinicia a hierarquia de precedência para tudo que estiver dentro dos parênteses.
* **Erro:** Se o token não for nenhum destes, lança erro de sintaxe.

---

### ⚙️ Métodos Auxiliares

Para navegar pela lista de tokens com segurança e aplicar as regras gramaticais, o Parser utiliza métodos auxiliares robustos.

#### `match(TokenType... types)` (Consumo Opcional)
Verifica se o token atual corresponde a algum dos tipos passados.
* **Lógica:** Se houver correspondência, ele consome o token (avança o ponteiro `current`) e retorna `true`. Se não, retorna `false` sem mexer no ponteiro.
* **Uso:** Usado para operadores opcionais (ex: verificar se há um `+` depois de um número).

#### `consume(TokenType type, String message)` (Consumo Obrigatório)
Verifica se o token atual é **exatamente** do tipo esperado.
* **Lógica:**
    * Se for igual: Consome e retorna o token.
    * Se for diferente: **Lança uma `RuntimeException`** com a mensagem de erro fornecida.
* **Uso:** Essencial para validar a estrutura sintática obrigatória (ex: garantir que um `(` seja fechado por um `)`). Isso atende diretamente à **Regra 5** (Detecção de erros).

#### `parseComplexString(String text)` (Conversor de Dados)
Como o Lexer entrega o número complexo como uma String bruta (ex: `"3+4i"`, `"-i"`, `"5"`), este método converte essa string em um objeto `ComplexNumber`.
* **Lógica:**
    1.  Limpa espaços em branco.
    2.  Trata casos isolados (`"i"`, `"-i"`, `"+i"`).
    3.  Busca o ponto de corte: varre a string de trás para frente procurando o último `+` ou `-` (que separa a parte real da imaginária).
    4.  **Se não achar corte:** É imaginário puro (ex: `"5i"`) ou real puro (ex: `"5"`).
    5.  **Se achar corte:** Divide a string em duas substrings, faz o *parsing* de cada parte para `double` e instancia o `ComplexNumber(real, imag)`.

#### `advance()`, `peek()`, `previous()`
Métodos de baixo nível para gerenciamento do ponteiro da lista.
* `peek()`: Olha o token atual sem consumir.
* `advance()`: Consome o token atual e move o ponteiro para o próximo.
* `previous()`: Retorna o último token consumido (útil para saber qual operador acabamos de passar).