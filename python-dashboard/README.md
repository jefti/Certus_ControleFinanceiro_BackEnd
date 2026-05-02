# Dashboard Excel Export (Python / FastAPI)

Servico Python que expoe um endpoint para exportar a dashboard financeira do
usuario em formato Excel (`.xlsx`). O frontend liga o botao "Baixar Excel" da
dashboard nesta API; ela consulta o backend Java usando o token JWT do usuario,
processa os titulos com Pandas e devolve a planilha pronta.

## Endpoint

`GET /dashboard/export`

- Header obrigatorio: `Authorization: Bearer <jwt>`
- Resposta: arquivo `dashboard.xlsx` com abas `Titulos` e `Resumo`.

## Variaveis de ambiente

- `JAVA_BACKEND_URL` (default `http://localhost:8080`): URL do backend Spring.

## Executar localmente

```bash
cd python-dashboard
pip install -r requirements.txt
uvicorn main:app --reload --port 8001
```

## Docker

```bash
docker build -t certus-dashboard-export ./python-dashboard
docker run --rm -p 8001:8001 -e JAVA_BACKEND_URL=http://host.docker.internal:8080 certus-dashboard-export
```

## Como usar pelo frontend

```js
const res = await fetch("http://localhost:8001/dashboard/export", {
  headers: { Authorization: `Bearer ${jwt}` },
});
const blob = await res.blob();
const url = URL.createObjectURL(blob);
const a = document.createElement("a");
a.href = url;
a.download = "dashboard.xlsx";
a.click();
```
