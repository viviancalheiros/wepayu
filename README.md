# WePayU

Projeto desenvolvido para a disciplina de **Programação 2**, utilizando Java.

## 📌 Descrição
O sistema **WePayU** consiste em um **sistema de folha de pagamento** de empregados de uma empresa.
Ele permite cadastrar diferentes tipos de empregados, registrar eventos como cartões de ponto e taxas de serviço, e calcular a folha de pagamento de acordo com as regras de cada categoria.

## 📁 Estrutura do sistema
Existem três tipos de empregados no sistema:
- **Horistas**
  - Recebem a partir de cartões de ponto.
  - O primeiro cartão lançado indica o dia de início dele.
- **Comissionados**
  - Recebem um salário fixo + comissão por vendas.
- **Assalariados**
  - Recebem apenas um salário fixo.
- **Descontos**
  - Os empregados podem ter descontos nos seus salários:
  - Caso sejam do sindicato:
    - Pagam **taxas diárias**.
    - Podem ter **taxas de serviço**.

## ⚙️ Funcionalidades
- Criar empregado
- Alterar dados do empregado
- Remover empregado do sistemna
- Buscar atributos do empregado
- Lançar cartão de ponto
- Lançar taxa de serviço
- Calcular folha de pagamento para determinado dia

## 💾 Persistência
O sistema utiliza **arquivos XML** para salvar e recuperar os dados dos empregados.
- Os empregados são salvos em `empregados.xml`
- Toda vez que o sistema carrega ele pega os dados desse arquivo
- Caso não encontre o arquivo, uma lista vazia é criada.
### 🔧 Métodos principais
1. `iniciarSistema()`
- Lê o arquivo empregados.xml usando XMLDecoder.
- Caso o arquivo não exista ou ocorra erro, inicializa uma lista vazia de empregados.
2. `encerrarSistema()`
- Salva os empregados em empregados.xml usando XMLEncoder.
- Garante que os dados fiquem persistidos entre execuções.
3. `zerarSistema()`
- Limpa todas as informações armazenadas (empregados e dados sindicais).
- Útil para recomeçar os testes ou reiniciar o estado do sistema.
