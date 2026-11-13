-- 004 — Lote de EXEMPLO para validar o caminho de leitura do dashboard.
--
-- TEMPORÁRIO, SÓ PARA TESTE. Insere um lote e uma estatística à mão, sem o
-- serviço Python, para conferir que DashboardController -> DashboardService ->
-- front funcionam ponta a ponta. Apague depois (último bloco do arquivo).
--
-- Gerado a partir de src/services/dashboard.fixture.js, então a tela deve
-- mostrar exatamente os mesmos números que o fixture mostrava — com a
-- diferença de que agora vêm do banco, passando pelo Spring.
--
-- NO DBEAVER: arquivo inteiro com "Execute SQL Script" (Alt+X).

INSERT INTO upload_lote (arquivo_nome, enviado_em, status, total_linhas,
                         linhas_validas, linhas_invalidas, linhas_com_aviso, mensagem)
VALUES ('carteira_exemplo.xlsx', TIMESTAMP '2026-11-12 19:40:00', 'PROCESSADO',
        12, 9, 3, 1, NULL);

-- Usa o lote recém-criado (o de maior id) e grava o JSON das estatísticas.
-- Dollar-quoting ($json$) para não precisar escapar nada dentro do JSON.
INSERT INTO estatistica_lote (id_lote, gerado_em, resultado)
SELECT max(id_lote), TIMESTAMP '2026-11-12 19:40:12', $json${
  "resumo": {
    "totalClientes": 9,
    "faturamentoTotal": 19970000,
    "media": 2218888.89,
    "mediana": 1480000,
    "desvioPadrao": 1890041.7,
    "minimo": 480000,
    "maximo": 5200000,
    "q1": 620000,
    "q3": 3900000,
    "segmentoMaisFrequente": "Comércio",
    "nivelMaisFrequente": "A",
    "servicoMaisContratado": "Internet Dedicada",
    "mediaServicosPorCliente": 1.89,
    "coeficienteVariacao": 0.852,
    "consultores": 4
  },
  "porSegmento": [
    {
      "segmento": "Comércio",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 11.5,
      "faturamentoTotal": 2290000,
      "total": 2290000,
      "media": 1145000,
      "mediana": 1145000,
      "desvioPadrao": 855598.2,
      "minimo": 540000,
      "maximo": 1750000
    },
    {
      "segmento": "Indústria",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 50.1,
      "faturamentoTotal": 10000000,
      "total": 10000000,
      "media": 5000000,
      "mediana": 5000000,
      "desvioPadrao": 282842.71,
      "minimo": 4800000,
      "maximo": 5200000
    },
    {
      "segmento": "Tecnologia",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 21.9,
      "faturamentoTotal": 4380000,
      "total": 4380000,
      "media": 2190000,
      "mediana": 2190000,
      "desvioPadrao": 2418305.2,
      "minimo": 480000,
      "maximo": 3900000
    },
    {
      "segmento": "Educação",
      "clientes": 1,
      "percentual": 11.1,
      "percentualFaturamento": 3.1,
      "faturamentoTotal": 620000,
      "total": 620000,
      "media": 620000,
      "mediana": 620000,
      "desvioPadrao": null,
      "minimo": 620000,
      "maximo": 620000
    },
    {
      "segmento": "Saúde",
      "clientes": 1,
      "percentual": 11.1,
      "percentualFaturamento": 7.4,
      "faturamentoTotal": 1480000,
      "total": 1480000,
      "media": 1480000,
      "mediana": 1480000,
      "desvioPadrao": null,
      "minimo": 1480000,
      "maximo": 1480000
    },
    {
      "segmento": "Serviços",
      "clientes": 1,
      "percentual": 11.1,
      "percentualFaturamento": 6,
      "faturamentoTotal": 1200000,
      "total": 1200000,
      "media": 1200000,
      "mediana": 1200000,
      "desvioPadrao": null,
      "minimo": 1200000,
      "maximo": 1200000
    }
  ],
  "porNivel": [
    {
      "nivel": "A",
      "clientes": 3,
      "percentual": 33.3,
      "percentualFaturamento": 69.6,
      "faturamentoTotal": 13900000,
      "total": 13900000,
      "media": 4633333.33,
      "mediana": 4800000,
      "desvioPadrao": 665833,
      "minimo": 3900000,
      "maximo": 5200000
    },
    {
      "nivel": "B",
      "clientes": 3,
      "percentual": 33.3,
      "percentualFaturamento": 22.2,
      "faturamentoTotal": 4430000,
      "total": 4430000,
      "media": 1476666.67,
      "mediana": 1480000,
      "desvioPadrao": 275015.1,
      "minimo": 1200000,
      "maximo": 1750000
    },
    {
      "nivel": "C",
      "clientes": 3,
      "percentual": 33.3,
      "percentualFaturamento": 8.2,
      "faturamentoTotal": 1640000,
      "total": 1640000,
      "media": 546666.67,
      "mediana": 540000,
      "desvioPadrao": 70237.69,
      "minimo": 480000,
      "maximo": 620000
    }
  ],
  "porConsultor": [
    {
      "consultor": "Ana Souza",
      "clientes": 3,
      "percentual": 33.3,
      "percentualFaturamento": 58.8,
      "faturamentoTotal": 11750000,
      "total": 11750000,
      "media": 3916666.67,
      "mediana": 4800000,
      "desvioPadrao": 1887015.9,
      "minimo": 1750000,
      "maximo": 5200000
    },
    {
      "consultor": "Bruno Lima",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 25.5,
      "faturamentoTotal": 5100000,
      "total": 5100000,
      "media": 2550000,
      "mediana": 2550000,
      "desvioPadrao": 1909188.3,
      "minimo": 1200000,
      "maximo": 3900000
    },
    {
      "consultor": "Carla Mendes",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 10.1,
      "faturamentoTotal": 2020000,
      "total": 2020000,
      "media": 1010000,
      "mediana": 1010000,
      "desvioPadrao": 664680.4,
      "minimo": 540000,
      "maximo": 1480000
    },
    {
      "consultor": "Diego Alves",
      "clientes": 2,
      "percentual": 22.2,
      "percentualFaturamento": 5.5,
      "faturamentoTotal": 1100000,
      "total": 1100000,
      "media": 550000,
      "mediana": 550000,
      "desvioPadrao": 98994.95,
      "minimo": 480000,
      "maximo": 620000
    }
  ],
  "porServico": [
    {
      "servico": "Internet Dedicada",
      "contratos": 7,
      "percentual": 77.8,
      "faturamentoMedio": 2612857.14
    },
    {
      "servico": "Cloud Backup",
      "contratos": 4,
      "percentual": 44.4,
      "faturamentoMedio": 2695000
    },
    {
      "servico": "Firewall",
      "contratos": 4,
      "percentual": 44.4,
      "faturamentoMedio": 3170000
    },
    {
      "servico": "Link Redundante",
      "contratos": 2,
      "percentual": 22.2,
      "faturamentoMedio": 5000000
    }
  ],
  "porMes": [
    {
      "mes": "2025-01",
      "clientes": 1,
      "faturamento": 4800000,
      "acumulado": 1
    },
    {
      "mes": "2025-02",
      "clientes": 1,
      "faturamento": 1750000,
      "acumulado": 2
    },
    {
      "mes": "2025-03",
      "clientes": 2,
      "faturamento": 5380000,
      "acumulado": 4
    },
    {
      "mes": "2025-04",
      "clientes": 0,
      "faturamento": 0,
      "acumulado": 4
    },
    {
      "mes": "2025-05",
      "clientes": 1,
      "faturamento": 620000,
      "acumulado": 5
    },
    {
      "mes": "2025-06",
      "clientes": 3,
      "faturamento": 6940000,
      "acumulado": 8
    },
    {
      "mes": "2025-07",
      "clientes": 0,
      "faturamento": 0,
      "acumulado": 8
    },
    {
      "mes": "2025-08",
      "clientes": 1,
      "faturamento": 480000,
      "acumulado": 9
    }
  ],
  "correlacao": {
    "pearson": 0.88,
    "p": 0.001742,
    "n": 9,
    "spearman": 0.816,
    "spearmanP": 0.007313,
    "interpretacao": "Correlação forte positiva entre a quantidade de serviços e o faturamento."
  },
  "topClientes": [
    {
      "codigo": "CLI-008",
      "nome": "Usina São Pedro",
      "segmento": "Indústria",
      "nivel": "A",
      "faturamento": 5200000
    },
    {
      "codigo": "CLI-001",
      "nome": "Metalúrgica Bragança",
      "segmento": "Indústria",
      "nivel": "A",
      "faturamento": 4800000
    },
    {
      "codigo": "CLI-002",
      "nome": "TechNorte Sistemas",
      "segmento": "Tecnologia",
      "nivel": "A",
      "faturamento": 3900000
    },
    {
      "codigo": "CLI-003",
      "nome": "Comercial Rosa Ltda",
      "segmento": "Comércio",
      "nivel": "B",
      "faturamento": 1750000
    },
    {
      "codigo": "CLI-004",
      "nome": "Clínica Vida Plena",
      "segmento": "Saúde",
      "nivel": "B",
      "faturamento": 1480000
    }
  ],
  "qualidade": {
    "linhasRecebidas": 12,
    "linhasValidas": 9,
    "linhasInvalidas": 3,
    "correcoesSegmento": 2,
    "correcoesConsultor": 1,
    "correcoesNivel": 1,
    "segmentosOriginais": [
      {
        "original": "COM.",
        "padronizado": "Comércio",
        "ocorrencias": 1
      },
      {
        "original": "IND.",
        "padronizado": "Indústria",
        "ocorrencias": 1
      },
      {
        "original": "industria",
        "padronizado": "Indústria",
        "ocorrencias": 1
      }
    ],
    "avisos": [
      "Linha 9: cidade e UF em branco."
    ]
  },
  "insights": [
    "O segmento Comércio concentra a maior carteira: 2 clientes (22,2%).",
    "A média de faturamento (R$ 2.218.888,89) é maior que a mediana (R$ 1.480.000,00): poucos clientes muito grandes puxam a média para cima, então a mediana descreve melhor o cliente típico.",
    "Os 3 clientes nível A representam 69,6% do faturamento total, com apenas 33,3% dos clientes.",
    "Ana Souza é o consultor com maior faturamento sob gestão (R$ 11.750.000,00, 3 clientes).",
    "Internet Dedicada é o serviço mais contratado: presente em 7 clientes (77,8%).",
    "O pico de contratações foi em 06/2025 (3 por mês).",
    "Correlação forte positiva entre a quantidade de serviços e o faturamento (r de Pearson = 0,88)."
  ]
}$json$
  FROM upload_lote;

-- Conferência: deve voltar 1 linha, status PROCESSADO, ~8380 bytes de JSON.
-- SELECT l.id_lote, l.arquivo_nome, l.status, e.gerado_em, length(e.resultado) AS bytes_json
--   FROM upload_lote l JOIN estatistica_lote e ON e.id_lote = l.id_lote;

-- ============================ LIMPEZA ============================
-- Rode quando o Python estiver gravando de verdade. A FK de estatistica_lote
-- tem ON DELETE CASCADE, então apagar o lote leva a estatística junto.
-- DELETE FROM upload_lote WHERE arquivo_nome = 'carteira_exemplo.xlsx';
