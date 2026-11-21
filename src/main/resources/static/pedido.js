const itens = [];
const campo = id => document.getElementById(id);
const moeda = valor => valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

function erro(id, mensagem) {
    const aviso = campo('erro-' + id);
    if (aviso) aviso.textContent = mensagem;
}

function limparErro(id) { erro(id, ''); }

function opcaoSelecionada(lista, valor) {
    return [...document.querySelectorAll(`#${lista} option`)].find(opcao => opcao.value === valor);
}

function adicionarItem() {
    limparErro('produto');
    limparErro('quantidade');
    limparErro('itens');
    const opcao = opcaoSelecionada('listaProdutos', campo('produto').value);
    const quantidade = Number(campo('quantidade').value);
    if (!opcao) erro('produto', 'Selecione um produto da lista.');
    if (!Number.isInteger(quantidade) || quantidade < 1 || quantidade > 1000)
        erro('quantidade', 'Informe uma quantidade entre 1 e 1000.');
    if (!opcao || !Number.isInteger(quantidade) || quantidade < 1 || quantidade > 1000) return;
    const produtoId = Number(opcao.value.split(' - ')[0]);
    const preco = Number(opcao.dataset.preco);
    if (!Number.isInteger(produtoId) || produtoId < 1 || !Number.isFinite(preco) || preco <= 0) {
        erro('produto', 'Selecione um produto válido.');
        return;
    }
    if (itens.length >= 100) {
        erro('itens', 'Adicione no máximo 100 produtos.');
        return;
    }
    itens.push({ produtoId, nomeProduto: opcao.value.split(' - ').slice(1).join(' - '), quantidade, preco });
    campo('produto').value = '';
    campo('quantidade').value = '';
    renderTabela();
}

function removerItem(indice) {
    itens.splice(indice, 1);
    renderTabela();
}

function alterarQuantidade(indice, valor) {
    const quantidade = Number(valor);
    limparErro('itens');
    if (!Number.isInteger(quantidade) || quantidade < 1 || quantidade > 1000) {
        erro('itens', 'Informe uma quantidade entre 1 e 1000.');
        renderTabela();
        return;
    }
    itens[indice].quantidade = quantidade;
    renderTabela();
}

function renderTabela() {
    const tabela = campo('tabela-itens');
    tabela.replaceChildren();
    if (!itens.length) {
        const linha = tabela.insertRow();
        const celula = linha.insertCell();
        celula.colSpan = 5;
        celula.textContent = 'Nenhum produto adicionado ao pedido';
    }
    itens.forEach((item, indice) => {
        const linha = tabela.insertRow();
        linha.insertCell().textContent = item.nomeProduto;
        const quantidade = document.createElement('input');
        quantidade.type = 'number';
        quantidade.min = '1';
        quantidade.max = '1000';
        quantidade.className = 'quantidade-tabela';
        quantidade.value = item.quantidade;
        quantidade.addEventListener('change', () => alterarQuantidade(indice, quantidade.value));
        linha.insertCell().appendChild(quantidade);
        linha.insertCell().textContent = moeda(item.preco);
        linha.insertCell().textContent = moeda(item.preco * item.quantidade);
        const remover = document.createElement('button');
        remover.type = 'button';
        remover.className = 'btn-remove';
        remover.textContent = 'Remover';
        remover.addEventListener('click', () => removerItem(indice));
        linha.insertCell().appendChild(remover);
    });
    campo('totalPedido').textContent = moeda(itens.reduce((total, item) => total + item.preco * item.quantidade, 0));
}

async function salvarPedido() {
    ['aluno', 'produto', 'quantidade', 'itens'].forEach(limparErro);
    const aluno = opcaoSelecionada('listaAlunos', campo('aluno').value);
    if (!aluno) erro('aluno', 'Selecione um aluno da lista.');
    if (!itens.length) erro('itens', 'Adicione ao menos um produto.');
    if (!aluno || !itens.length) return;
    const alunoId = Number(aluno.value.split(' - ')[0]);
    try {
        const resposta = await fetch('/pedidos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ alunoId, itens: itens.map(item => ({ produtoId: item.produtoId, quantidade: item.quantidade })) })
        });
        const dados = await resposta.json();
        if (!resposta.ok) {
            erro(dados.campo || 'itens', dados.erro || 'Não foi possível salvar o pedido.');
            return;
        }
        location.reload();
    } catch {
        erro('itens', 'Não foi possível salvar o pedido. Tente novamente.');
    }
}

renderTabela();
