# ----- Estágio 1: Build (Compilação) -----
# Usamos a imagem oficial do SDK (Kit de Desenvolvimento) do .NET 8.0
# Esta imagem contém o compilador e todas as ferramentas de build.
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /src

# Copia o arquivo .csproj primeiro e restaura os pacotes (NuGet)
# Isso é uma otimização de cache: se os pacotes não mudarem, o Docker
# reutiliza esta camada, acelerando builds futuros.
COPY *.csproj .
RUN dotnet restore

# Copia todo o restante do código-fonte para o container
COPY . .

# Compila e "publica" a aplicação em modo de Release (otimizado)
# A saída será colocada na pasta /app/publish
RUN dotnet publish -c Release -o /app/publish

# ----- Estágio 2: Final (Execução) -----
# Agora, usamos uma imagem "runtime" (de execução) do .NET 8.0
# Esta imagem é MUITO menor que a SDK, pois contém apenas o necessário
# para RODAR a aplicação, não para compilá-la.
FROM mcr.microsoft.com/dotnet/runtime:8.0 AS final

WORKDIR /app
# Copia APENAS os arquivos compilados do estágio "build" para a imagem final
COPY --from=build /app/publish .

# Define o "Ponto de Entrada" (Entrypoint)
# Este é o comando que será executado quando o container iniciar.
# Ele basicamente executa: dotnet CalculadoraComplexa.dll
ENTRYPOINT ["dotnet", "CalculadoraComplexa.dll"]