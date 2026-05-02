"""FastAPI service that exports the user's dashboard data to Excel.

Receives the caller's bearer token, fetches titulos from the Java backend,
builds an Excel workbook with Pandas and returns it as a download.
"""

from __future__ import annotations

import io
import os
from typing import Any

import httpx
import pandas as pd
from fastapi import FastAPI, Header, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse

JAVA_BACKEND_URL = os.getenv("JAVA_BACKEND_URL", "http://localhost:8080")
TITULOS_ENDPOINT = f"{JAVA_BACKEND_URL}/api/titulos/obter"

app = FastAPI(
    title="Dashboard Excel Export",
    description="Exporta a dashboard financeira do usuario para Excel",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET"],
    allow_headers=["*"],
)


def _flatten_titulo(titulo: dict[str, Any]) -> dict[str, Any]:
    centros = titulo.get("centrosDeCusto") or []
    return {
        "ID": titulo.get("id"),
        "Descricao": titulo.get("descricao"),
        "Valor": titulo.get("valor"),
        "Data Vencimento": titulo.get("dataVencimento"),
        "Data Pagamento": titulo.get("dataPagamento"),
        "Tipo": titulo.get("tipo"),
        "Centros de Custo": ", ".join(c.get("descricao", "") for c in centros),
    }


def _build_excel(titulos: list[dict[str, Any]]) -> bytes:
    rows = [_flatten_titulo(t) for t in titulos]
    df = pd.DataFrame(rows) if rows else pd.DataFrame(
        columns=[
            "ID", "Descricao", "Valor", "Data Vencimento",
            "Data Pagamento", "Tipo", "Centros de Custo",
        ]
    )

    buffer = io.BytesIO()
    with pd.ExcelWriter(buffer, engine="openpyxl") as writer:
        df.to_excel(writer, sheet_name="Titulos", index=False)

        if not df.empty and "Valor" in df.columns:
            valores = pd.to_numeric(df["Valor"], errors="coerce")
            resumo = pd.DataFrame(
                [
                    {"Metrica": "Quantidade de titulos", "Valor": len(df)},
                    {"Metrica": "Valor total", "Valor": float(valores.sum())},
                    {"Metrica": "Valor medio", "Valor": float(valores.mean())},
                ]
            )
            resumo.to_excel(writer, sheet_name="Resumo", index=False)

    buffer.seek(0)
    return buffer.getvalue()


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.get("/dashboard/export")
async def export_dashboard(authorization: str | None = Header(default=None)) -> StreamingResponse:
    if not authorization:
        raise HTTPException(status_code=401, detail="Authorization header obrigatorio")

    async with httpx.AsyncClient(timeout=30.0) as client:
        try:
            response = await client.get(
                TITULOS_ENDPOINT,
                headers={"Authorization": authorization},
            )
        except httpx.HTTPError as exc:
            raise HTTPException(status_code=502, detail=f"Falha ao consultar backend: {exc}") from exc

    if response.status_code == 401:
        raise HTTPException(status_code=401, detail="Token invalido ou expirado")
    if response.status_code >= 400:
        raise HTTPException(status_code=502, detail=f"Backend retornou {response.status_code}")

    excel_bytes = _build_excel(response.json())

    return StreamingResponse(
        io.BytesIO(excel_bytes),
        media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        headers={"Content-Disposition": 'attachment; filename="dashboard.xlsx"'},
    )
