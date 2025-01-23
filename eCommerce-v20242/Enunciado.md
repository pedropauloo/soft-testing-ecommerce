# Testes de Software - Prof. Eiji Adachi

**Trabalho**: Implementação de Testes Automatizados para Funcionalidade de Finalização de Compra em um E-commerce  

---

#### Contexto do Trabalho

Você recebeu um projeto backend de uma aplicação de e-commerce estruturada como uma **API REST**, organizada em três camadas principais:
- **Controller**: Responsável por lidar com as requisições HTTP.
- **Service**: Contém a lógica de negócio.
- **Repository**: Responsável pela interação com o banco de dados.

Neste trabalho, o foco será implementar **testes automatizados** para a funcionalidade de **finalizar compra**, que faz parte de um processo de checkout em um e-commerce. 

Este trabalho deverá ser feito pelo mesmo grupo da 2a unidade.

#### A Funcionalidade de Finalização de Compra

A funcionalidade de finalizar compra recebe como entradas um identificador para um **cliente** e um identificador para um **carrinho de compras**, que encapsula um conjunto de produtos selecionados pelo cliente. Ela realiza as seguintes operações:

1. **Consulta ao serviço de estoque**: Verifica se há quantidade suficiente de cada produto no estoque.
2. **Cálculo do preço total da compra**: Soma o valor total dos produtos no carrinho e aplica as regras de negócio descritas a seguir.
3. **Consulta ao serviço de pagamentos**: Verifica se o pagamento foi autorizado.
4. **Atualização do estoque**: Se o pagamento for autorizado, dá baixa no estoque, reduzindo a quantidade dos produtos.

Seu objetivo é implementar testes que cubram a funcionalidade de **finalizar compra** de forma isolada e integrada.


#### Regras para cálculo do custo da compra
O custo de uma compra é composto pelo custo dos produtos e o custo do frete.

O valor do frete é calculado com base no peso total de todos itens comprados: até 5 kg não é cobrado frete; acima de 5 kg e abaixo de 10 kg é cobrado R$ 2,00 por kg; acima de 10 kg e abaixo de 50 kg é cobrado R$ 4,00 por kg; e acima de 50 kg é cobrado R$ 7,00 por kg. Além disso, clientes do tipo Ouro possuem isenção total do valor do frete, do tipo Prata possuem desconto de 50% e do tipo Bronze pagam o valor integral.

Carrinhos de compras que custam mais de R$ 500,00 recebem um desconto de 10% e aqueles que custam mais de R$ 1000,00 recebem 20% de desconto. Vale ressaltar que este desconto é aplicado somente ao valor dos itens, excluindo o valor do frete.

#### Objetivos do Trabalho
   - Implementar a lógica de negócio do cálculo do custo (método `calcularCustoTotal` da classe `CompraService`).
   - Implementar testes automatizados para o **método de cálculo do preço total da compra** na camada de serviço.  
   - Atingir 100% de cobertura de arestas.
   - Matar todos os mutantes gerados pela ferramenta [PIT](https://pitest.org ) configurada para rodar com os operadores mutantes definidos para o grupo [All](https://pitest.org/quickstart/mutators/).


#### Critérios de Avaliação

1. **Correção dos Testes**: Verificar se os testes cobrem adequadamente os diferentes cenários e se validam corretamente o comportamento esperado da aplicação.
2. **Cobertura de Testes**: Avaliar a cobertura de código, em especial nos testes de caixa branca para o cálculo do preço.
3. **Cobertura de Mutantes**: Avaliar a cobertura de mutantes, em especial se todos os mutantes gerados foram mortos. Caso algum mutante não seja morto, deve constar alguma justificativa no README do projeto.
4. **Organização e Boas Práticas**: Avaliar a organização do código dos testes, seguindo boas práticas de nomenclatura, estrutura de testes e clareza.

#### Entrega
Você deverá entregar esta atividade como um projeto nomeado com os nomes dos membros seguindo o padrão nome1-nome2-nome3 (ex.: JoaoSilva-JoseSouza-EijiAdachi). Lembre-se de usar essa mesma string para alterar o nome do projeto no atributo artifactId do arquivo pom.xml. Este projeto deverá ser compactado em formato .zip e entregue via SIGAA.

É obrigatório um arquivo do tipo README.md descrevendo como executar o projeto, como executar os testes e como verificar a cobertura dos testes. Também é obrigatório o projeto estar devidamente configurado para rodar o JUnit 5, a ferramenta PIT configurada para usar o grupo All de operadores mutantes e estar devidamente configurada como um projeto Maven (já está, é só não bagunçar).