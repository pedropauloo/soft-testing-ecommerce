# 🛒 Projeto: Finalização de Compra - Testes Automatizados

Este é o projeto da funcionalidade **Finalização de Compra** do e-commerce, desenvolvido para a disciplina **Testes de Software** ministrada pelo professor **Eiji Adachi**.

---

## 🎯 Objetivo do Projeto

O objetivo principal é implementar e testar a funcionalidade de **cálculo do custo total da compra** na camada de serviço. Isso inclui:

1. **Implementar a funcionalidade** de cálculo do custo total considerando frete, descontos e regras de negócio.
2. **Criar testes automatizados** para garantir a cobertura completa do código.
3. **Executar e analisar a cobertura de mutantes** utilizando a ferramenta [PIT](https://pitest.org).

---

## 🗂️ Estrutura do Projeto

O projeto segue a arquitetura em três camadas:

- **🌐 Controller**: Responsável por receber requisições HTTP e retorná-las ao cliente.
- **🧠 Service**: Contém a lógica de negócio da funcionalidade de finalização de compra.
- **💾 Repository**: Gerencia a interação com o banco de dados.

Os testes estão localizados em:

- `src/test/java`: Testes unitários para a camada de serviço.

---

## 🛠️ Requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

- **☕ Java**: Versão 8 ou superior
  - Verifique a versão com o comando: `java -version`
- **🐍 Maven**: Gerenciador de dependências
  - Verifique a versão com o comando: `mvn -v`
- **🧪 JUnit 5**: Framework de testes automatizados
- **🧬 PIT**: Ferramenta de testes de mutação

---

## ▶️ Como Executar o Projeto

1. Clone o repositório para sua máquina local:

   ```bash
   git clone https://github.com/pedropauloo/soft-testing-ecommerce.git
   ```

2. Navegue até a pasta do projeto:

   ```bash
   cd soft-testing-ecommerce
   ```

3. Compile o projeto utilizando o Maven:

   ```bash
   mvn clean install
   ```

4. (Opcional) Para executar a aplicação, utilize:

   ```bash
   mvn spring-boot:run
   ```

---

## ✅ Como Executar os Testes

Para executar os testes automatizados, utilize o seguinte comando:

```bash
mvn test
```

Verifique no terminal se todos os testes passaram com sucesso.

---

## 📊 Como Verificar a Cobertura de Testes

1. Certifique-se de que a ferramenta Jacoco está configurada no `pom.xml`.
2. Para gerar o relatório de cobertura, execute:
   ```bash
   mvn jacoco:report
   ```
3. O relatório será gerado no caminho:
   ```
   target/site/jacoco/index.html
   ```

Abra o arquivo HTML no navegador para visualizar a cobertura detalhada.

---

## 🧪 Como Executar a Análise de Mutação com PIT

1. Execute o comando para rodar o PIT:
   ```bash
   mvn org.pitest:pitest-maven:mutationCoverage
   ```
2. O relatório de mutantes será gerado no caminho:
   ```
   target/pit-reports/index.html
   ```

---

## 👥 Autores

- José Ben Hur Nascimento de Oliveira - 20240078121
- Pedro Paulo Lucas de Lira - 20220043307
- Samuel de Araújo Costa - Matrícula

---

## 📜 Licença

Este projeto foi desenvolvido para fins acadêmicos e não possui licença oficial.
