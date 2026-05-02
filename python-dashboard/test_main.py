from main import _build_excel, _flatten_titulo


def test_flatten_titulo_joins_centros():
    titulo = {
        "id": 1,
        "descricao": "Aluguel",
        "valor": 1500.50,
        "dataVencimento": "2026-05-10",
        "dataPagamento": None,
        "tipo": "DESPESA",
        "centrosDeCusto": [
            {"id": 1, "descricao": "Casa"},
            {"id": 2, "descricao": "Fixo"},
        ],
    }
    row = _flatten_titulo(titulo)
    assert row["ID"] == 1
    assert row["Descricao"] == "Aluguel"
    assert row["Centros de Custo"] == "Casa, Fixo"


def test_build_excel_returns_xlsx_bytes():
    data = [
        {
            "id": 1, "descricao": "A", "valor": 10,
            "dataVencimento": "2026-01-01", "dataPagamento": None,
            "tipo": "RECEITA", "centrosDeCusto": [],
        },
        {
            "id": 2, "descricao": "B", "valor": 20,
            "dataVencimento": "2026-02-01", "dataPagamento": None,
            "tipo": "DESPESA", "centrosDeCusto": [],
        },
    ]
    content = _build_excel(data)
    assert content[:2] == b"PK"
    assert len(content) > 0


def test_build_excel_handles_empty_list():
    content = _build_excel([])
    assert content[:2] == b"PK"
