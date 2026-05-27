const apiDestinatarios = "/api/destinatarios";
const apiEnvios = "/api/envios";
const apiLogs = "/api/logs";

const formDestinatario = document.getElementById("formDestinatario");
const msgDestinatario = document.getElementById("msgDestinatario");
const tabelaDestinatarios = document.querySelector("#tabelaDestinatarios tbody");
const btnRecarregar = document.getElementById("btnRecarregar");

const formEnvio = document.getElementById("formEnvio");
const msgEnvio = document.getElementById("msgEnvio");

const btnLogs = document.getElementById("btnLogs");
const tabelaLogs = document.querySelector("#tabelaLogs tbody");

function exibirMensagem(elemento, texto, ok) {
    elemento.textContent = texto;
    elemento.className = "msg " + (ok ? "ok" : "erro");
}

async function carregarDestinatarios() {
    try {
        const resposta = await fetch(apiDestinatarios);
        const dados = await resposta.json();
        tabelaDestinatarios.innerHTML = "";
        dados.forEach(d => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${d.id}</td>
                <td>${d.nome}</td>
                <td>${d.email}</td>
                <td>${new Date(d.criadoEm).toLocaleString("pt-BR")}</td>
                <td><button class="btn-remover" data-id="${d.id}">Remover</button></td>
            `;
            tabelaDestinatarios.appendChild(tr);
        });
    } catch (err) {
        exibirMensagem(msgDestinatario, "Erro ao carregar lista", false);
    }
}

formDestinatario.addEventListener("submit", async (e) => {
    e.preventDefault();
    const nome = document.getElementById("nome").value.trim();
    const email = document.getElementById("email").value.trim();
    try {
        const resposta = await fetch(apiDestinatarios, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome, email })
        });
        if (!resposta.ok) {
            const erro = await resposta.json();
            exibirMensagem(msgDestinatario, erro.erro || "Erro ao cadastrar", false);
            return;
        }
        formDestinatario.reset();
        exibirMensagem(msgDestinatario, "Destinatário cadastrado com sucesso", true);
        carregarDestinatarios();
    } catch (err) {
        exibirMensagem(msgDestinatario, "Erro de comunicação com o servidor", false);
    }
});

tabelaDestinatarios.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("btn-remover")) return;
    const id = e.target.getAttribute("data-id");
    try {
        const resposta = await fetch(`${apiDestinatarios}/${id}`, { method: "DELETE" });
        if (resposta.ok) {
            exibirMensagem(msgDestinatario, "Destinatário removido", true);
            carregarDestinatarios();
        } else {
            exibirMensagem(msgDestinatario, "Erro ao remover", false);
        }
    } catch (err) {
        exibirMensagem(msgDestinatario, "Erro de comunicação", false);
    }
});

btnRecarregar.addEventListener("click", carregarDestinatarios);

formEnvio.addEventListener("submit", async (e) => {
    e.preventDefault();
    const assunto = document.getElementById("assunto").value.trim();
    const corpo = document.getElementById("corpo").value.trim();
    try {
        const resposta = await fetch(apiEnvios, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ assunto, corpo })
        });
        const dados = await resposta.json();
        if (!resposta.ok) {
            exibirMensagem(msgEnvio, dados.erro || "Erro ao publicar na fila", false);
            return;
        }
        exibirMensagem(msgEnvio,
            `${dados.mensagem} (${dados.totalDestinatarios} destinatários)`, true);
        formEnvio.reset();
    } catch (err) {
        exibirMensagem(msgEnvio, "Erro de comunicação com o servidor", false);
    }
});

async function carregarLogs() {
    try {
        const resposta = await fetch(apiLogs);
        const dados = await resposta.json();
        tabelaLogs.innerHTML = "";
        dados.forEach(l => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${l.id}</td>
                <td>${l.destinatario}</td>
                <td>${l.assunto}</td>
                <td>${l.status}</td>
                <td>${l.detalhe || ""}</td>
                <td>${new Date(l.processadoEm).toLocaleString("pt-BR")}</td>
            `;
            tabelaLogs.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

btnLogs.addEventListener("click", carregarLogs);

carregarDestinatarios();
carregarLogs();
