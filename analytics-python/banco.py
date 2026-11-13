"""Banco: GRAVA no PostgreSQL (Azure) os clientes, contratos e o JSON do dashboard.

ADAPTADO para o schema do Norte Data. O arquivo original do professor foi escrito para o
schema normalizado dele e NÃO roda aqui — as diferenças estão listadas abaixo. Se for comparar
com o material da aula, é aqui que as duas versões divergem (os outros .py são idênticos).

  | professor                         | Norte Data                                        |
  |-----------------------------------|---------------------------------------------------|
  | upload_lote.id                    | upload_lote.id_lote                               |
  | cliente.lote_id / .id             | cliente.id_lote / .id_cliente                     |
  | cliente.codigo_cti                | cliente.codigo_cliente                            |
  | cliente.nome                      | cliente.nome_empresa                              |
  | cliente.consultor_id              | cliente.id_consultor                              |
  | cliente.segmento_id -> tabela     | cliente.segmento é TEXTO (não existe tabela)      |
  | (não tem)                         | cliente.status NOT NULL -> gravamos 'ATIVO'       |
  | consultor.id / (nome)             | consultor.id_consultor                            |
  | servico.nome                      | servico.descricao + servico.valor NOT NULL        |
  | contrato.cliente_id / .servico_id | contrato.id_cliente / .id_servico                 |
  | estatistica_lote.lote_id          | estatistica_lote.id_lote                          |

Pré-requisitos de schema (Backend/sql/): 003 criou estatistica_lote e o índice único em
consultor(nome); 005 criou o índice único em servico(descricao). Sem eles os ON CONFLICT falham.
"""

from __future__ import annotations                                    # Recurso do Python para aceitar tipos mais flexíveis nas dicas de código
import json                                                           # json: converte dicionários do Python em formato de texto JSON (e vice-versa)
import logging                                                        # logging: escreve mensagens e alertas no terminal do sistema
from datetime import datetime, timezone                               # datetime e timezone: manipulam data e hora no padrão UTC (sem fuso horário)

import pandas as pd                                                   # pandas: biblioteca usada para lidar com tabelas de dados (DataFrames)
from sqlalchemy import create_engine, text                            # SQLAlchemy: create_engine abre a conexão com o banco; text executa comandos SQL
from sqlalchemy.engine import Connection, Engine                      # Tipos especiais do SQLAlchemy usados apenas para indicar os tipos dos dados

from config import database_url                                       # config: carrega a função que busca o endereço secreto do banco no arquivo .env

log = logging.getLogger("analytics")                                  # Cria o registrador de logs específico para acompanhar o salvamento no banco

# Larguras reais das colunas no Norte Data. O corte evita que um valor longo da planilha
# estoure a coluna e derrube o lote inteiro no meio da transação.
MAX_CODIGO_CLIENTE = 50                                               # cliente.codigo_cliente VARCHAR(50)
MAX_NOME_EMPRESA = 180                                                # cliente.nome_empresa VARCHAR(180)
MAX_SEGMENTO = 100                                                    # cliente.segmento VARCHAR(100)
MAX_CIDADE = 100                                                      # cliente.cidade VARCHAR(100)
MAX_CONSULTOR = 150                                                   # consultor.nome VARCHAR(150)
MAX_SERVICO = 150                                                     # servico.descricao VARCHAR(150)


# --- BLOCO 2: CONEXÃO E GERENCIAMENTO DE DATA/HORA ---
def criar_engine() -> Engine:                                         # criar_engine: abre e retorna a "porta" de conexão com o banco PostgreSQL no Azure
    return create_engine(database_url(), pool_pre_ping=True)          # pool_pre_ping testa se a conexão ainda está viva antes de usá-la

def agora_utc() -> datetime:                                          # agora_utc: pega a data e hora atual no padrão UTC
    """O Spring Boot grava TIMESTAMP em UTC (sem fuso); fazemos o mesmo."""
    return datetime.now(timezone.utc).replace(tzinfo=None)            # Remove a informação de fuso horário para o PostgreSQL salvar sem conflitos


# --- BLOCO 3: CRIAÇÃO E CONTROLE DO LOTE DE UPLOAD ---
def criar_lote(engine: Engine, arquivo_nome: str, recebidas: int,     # criar_lote: o PYTHON cria o lote (o Java envia, mas o Python registra o andamento)
               invalidas: int, validas: int, com_aviso: int) -> int:  # Recebe os números de linhas totais, válidas, com aviso e inválidas
    """Cria a linha em upload_lote com status PROCESSANDO e devolve o id novo."""
    with engine.begin() as conn:                                      # Abre uma transação (salva tudo de vez ou cancela se der erro ao sair do bloco)
        return conn.execute(                                          # Executa o comando SQL de inserção e já pede o ID gerado para este lote
            text("INSERT INTO upload_lote (arquivo_nome, enviado_em, status, total_linhas, "
                 "linhas_validas, linhas_invalidas, linhas_com_aviso, mensagem) "
                 "VALUES (:a, :e, 'PROCESSANDO', :t, :v, :i, :c, 'ETL em andamento...') "
                 "RETURNING id_lote"),                                # ADAPTADO: a PK aqui é id_lote, não id
            {"a": arquivo_nome[:255], "e": agora_utc(), "t": recebidas,
             "v": validas, "i": invalidas, "c": com_aviso}
        ).scalar_one()                                                # Pega apenas o número do ID gerado pelo banco e o devolve

def marcar_erro(engine: Engine, lote_id: int, mensagem: str) -> None: # marcar_erro: atualiza o status do lote para 'ERRO' se algo der errado
    with engine.begin() as conn:                                      # Abre uma transação no banco de dados
        conn.execute(text("UPDATE upload_lote SET status = 'ERRO', mensagem = :m WHERE id_lote = :id"),
                     {"id": lote_id, "m": mensagem[:480]})            # Limita a mensagem (a coluna é VARCHAR(500))


# --- BLOCO 4: TABELAS DE APOIO (consultor e servico) ---
# O professor usa uma função genérica id_de(tabela, nome) porque as tabelas dele têm todas a
# mesma forma (id + nome). Aqui cada uma tem PK e colunas próprias, então ficam separadas.

def _id_consultor(conn: Connection, nome: str, cache: dict[str, int]) -> int:
    """Acha o consultor pelo nome ou cria um novo. ON CONFLICT depende do índice único de 003."""
    if nome not in cache:
        # Consultor aqui NÃO é entidade de login (a autenticação roda em `usuario`): email e
        # senha são nullable justamente para o ETL poder criar consultor só com o nome.
        conn.execute(text("INSERT INTO consultor (nome) VALUES (:n) ON CONFLICT (nome) DO NOTHING"),
                     {"n": nome})
        cache[nome] = conn.execute(text("SELECT id_consultor FROM consultor WHERE nome = :n"),
                                   {"n": nome}).scalar_one()
    return cache[nome]

def _id_servico(conn: Connection, descricao: str, cache: dict[str, int]) -> int:
    """Acha o serviço pela descrição ou cria um novo. ON CONFLICT depende do índice único de 005."""
    if descricao not in cache:
        # servico.valor é NOT NULL e a planilha não traz preço — entra 0. Quem cadastrar o
        # serviço pelo CRUD depois é que define o valor de verdade.
        conn.execute(text("INSERT INTO servico (descricao, valor) VALUES (:d, 0) "
                          "ON CONFLICT (descricao) DO NOTHING"), {"d": descricao})
        cache[descricao] = conn.execute(text("SELECT id_servico FROM servico WHERE descricao = :d"),
                                        {"d": descricao}).scalar_one()
    return cache[descricao]


# --- BLOCO 5: GRAVAÇÃO DOS DADOS TRATADOS (CARGA COMPLETA) ---
def gravar_resultado(engine: Engine, lote_id: int, tratado: pd.DataFrame, payload: dict, rejeitadas_etl: int) -> None:
    """Grava tudo em uma única transação: ou o lote inteiro é processado, ou nada muda."""
    consultores: dict[str, int] = {}                                  # Cache: nome do consultor -> id no banco
    servicos: dict[str, int] = {}                                     # Cache: descrição do serviço -> id no banco

    with engine.begin() as conn:                                      # Uma transação só: ou grava tudo com sucesso ou desfaz tudo se houver falha
        # Passo 5.1: Limpeza prévia, para reprocessar o mesmo lote sem duplicar.
        # ADAPTADO: a FK contrato -> cliente NÃO tem ON DELETE CASCADE aqui, então os contratos
        # precisam sair ANTES dos clientes, senão o DELETE abaixo viola a chave estrangeira.
        # O filtro por id_lote protege os clientes cadastrados à mão pelo CRUD (id_lote NULL).
        conn.execute(text("DELETE FROM contrato WHERE id_cliente IN "
                          "(SELECT id_cliente FROM cliente WHERE id_lote = :l)"), {"l": lote_id})
        conn.execute(text("DELETE FROM cliente WHERE id_lote = :l"), {"l": lote_id})
        conn.execute(text("DELETE FROM estatistica_lote WHERE id_lote = :l"), {"l": lote_id})

        # Passo 5.2: Inserção dos dados de cada cliente limpo
        for r in tratado.itertuples(index=False):                     # Percorre linha por linha da tabela de dados limpos (DataFrame)
            cliente_id = conn.execute(text("""
                INSERT INTO cliente (id_lote, codigo_cliente, nome_empresa, id_consultor,
                                     segmento, nivel, status, faturamento_anual, cidade, uf)
                VALUES (:lote, :codigo, :nome, :consultor,
                        :segmento, :nivel, 'ATIVO', :fat, :cidade, :uf)
                RETURNING id_cliente
            """), {
                "lote": lote_id,
                "codigo": r.codigo_cliente[:MAX_CODIGO_CLIENTE],
                "nome": r.nome_cliente[:MAX_NOME_EMPRESA],            # ADAPTADO: a coluna chama nome_empresa
                "consultor": _id_consultor(conn, r.consultor[:MAX_CONSULTOR], consultores),
                "segmento": r.segmento[:MAX_SEGMENTO],                # ADAPTADO: texto, não FK para uma tabela segmento
                "nivel": r.nivel_cliente,                             # enum NivelCliente gravado como string: A, B ou C
                "fat": round(float(r.faturamento_anual), 2),
                "cidade": r.cidade[:MAX_CIDADE] if isinstance(r.cidade, str) else None,
                "uf": r.uf if isinstance(r.uf, str) else None,
            }).scalar_one()                                           # ADAPTADO: RETURNING id_cliente

            # Passo 5.3: Inserção dos contratos associados ao cliente
            for nome_servico in r.servicos:                           # Para cada serviço que o cliente contratou...
                conn.execute(text("""
                    INSERT INTO contrato (id_cliente, id_servico, data_contratacao)
                    VALUES (:c, :s, :d)
                """), {"c": cliente_id,
                       "s": _id_servico(conn, nome_servico[:MAX_SERVICO], servicos),
                       "d": r.data_contratacao.date()})               # Salva apenas a data da contratação (sem horário)

        # Passo 5.4: Salvamento das estatísticas consolidadas em formato JSON
        conn.execute(text("INSERT INTO estatistica_lote (id_lote, gerado_em, resultado) VALUES (:l, :g, :r)"),
                     {"l": lote_id, "g": agora_utc(), "r": json.dumps(payload, ensure_ascii=False)})

        # Passo 5.5: Atualização final do status do lote para sucesso
        mensagem = f"ETL concluído: {len(tratado)} clientes tratados"
        if rejeitadas_etl:                                            # Se houve alguma linha rejeitada durante a limpeza...
            mensagem += f", {rejeitadas_etl} linhas recusadas na limpeza"
        conn.execute(text("UPDATE upload_lote SET status = 'PROCESSADO', mensagem = :m WHERE id_lote = :id"),
                     {"id": lote_id, "m": mensagem + "."})

    # Passo 5.6: Registro final no terminal do servidor
    log.info("Lote %s gravado: %s clientes, %s consultores, %s serviços",
             lote_id, len(tratado), len(consultores), len(servicos))
