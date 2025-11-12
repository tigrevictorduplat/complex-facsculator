#  Calculadora Científica de Números Complexos (A3)

Este repositório contém o código-fonte do Trabalho A3: uma calculadora científica de números complexos em C#, capaz de analisar e executar expressões matemáticas.

O projeto segue estritamente as regras definidas na especificação do trabalho, incluindo a análise de expressões com árvores (AST) e a exibição da árvore em notação LISP.

## 👥 Integrantes

* **Víctor Duplat Tigre** 
* **Jorge Eduardo**

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** C# (.NET 8.0)
* **Ambiente:** Docker 

---

## 🚀 Como Executar (via Docker)

O projeto é "containerizado" para garantir que ele funcione em qualquer máquina que tenha o Docker instalado, sem a necessidade de configurar o ambiente .NET localmente.

### 1. Pré-requisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (ou Docker Engine no Linux).

### 2. Construir (Build) a Imagem

Clone este repositório, abra um terminal na pasta raiz do projeto e execute o comando de build:

```bash
docker build -t complex-facsculator .