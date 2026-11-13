# Ciência de dados — Python (pandas + FastAPI)

Recebe do backend as linhas **já validadas** e faz o ETL:
- **limpeza.py** padroniza (`ana souza` → `Ana Souza`, `IND.` → `Indústria`, texto → número e data)
- **analise.py** calcula (média, mediana, desvio padrão, totais por segmento, nível, consultor e serviço)
- **insights.py** escreve as frases
- **banco.py** grava no PostgreSQL (lote, cliente, consultor, serviço, contrato e o resultado das contas)

O Python **não devolve dados**: responde só `{"ok": true}`.

> ## Adaptado para o Norte Data
>
> Esta pasta veio do material da Aula 14. **Só o `banco.py` foi modificado** — os outros `.py`
> são idênticos aos do professor. O schema do Norte Data usa outros nomes de coluna
> (`id_lote`, `codigo_cliente`, `nome_empresa`, `id_consultor`, `id_cliente`, `id_servico`),
> `segmento` é **texto** em vez de FK para uma tabela `segmento`, `servico` tem
> `descricao`/`valor` em vez de `nome`, e `cliente.status` é NOT NULL. O cabeçalho do
> `banco.py` traz a tabela completa de equivalências.
>
> **Pré-requisitos de banco** (rodar no DBeaver antes do primeiro ETL):
> `Backend/sql/003_consultor_etl_estatistica.sql` e `Backend/sql/005_servico_descricao_unica.sql`.
> Sem os índices únicos de `consultor(nome)` e `servico(descricao)`, os `ON CONFLICT` do
> `banco.py` falham.

## Rodar
```
cd analytics-python
python -m venv .venv
.venv\Scripts\activate            (Mac/Linux: source .venv/bin/activate)
pip install -r requirements.txt
copy .env.example .env            (preencha DB_USER e DB_PASSWORD — as mesmas do backend Java)
uvicorn api:app --port 8000
```
Teste: http://localhost:8000/saude → `{"status":"ok"}` e http://localhost:8000/docs (tela de teste; use `/testar` com `amostras/clientes.json`, não grava nada).

## Arquivos
```
api.py          recebe o POST /etl e chama o main.py
main.py         coordena: cria o lote, limpa, calcula e grava
limpeza.py      limpa e padroniza
analise.py      faz as contas
insights.py     escreve as frases
banco.py        único arquivo que fala com o banco
config.py       lê o .env
```

## Deploy
Comando: `uvicorn api:app --host 0.0.0.0 --port $PORT` (rode dentro da pasta `analytics-python`).
Variáveis: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SSLMODE=require`, `ETL_API_KEY`.

Passo a passo completo: `../docs/CIENCIA-DE-DADOS.txt`
