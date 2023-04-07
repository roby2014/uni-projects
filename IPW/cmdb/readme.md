## Chelas Movies API

Relatório está no ficheiro [`docs/report.md`](./docs/report.md).

## Setup
```
git clone git@github.com:isel-leic-ipw/cmdb-ipw-leic-2223i-ipw32d-g12.git
cd cmdb-ipw-leic-2223i-ipw32d-g12
npm install
```

Instalar o ElasticSearch, meter o `xpack.security.enabled` a false no `config/elasticsearch.yml`, e correr o `bin/elasticsearch`.

## Comandos

`npm start` - Inicia o servidor.

`npm run dev` - Inicia o servidor, porém reanicia automaticamente depois de algum ficheiro mudar (usar este para desenvolvimento).

`npm test` - Corre os testes.

## Variáveis de ambiente

O ficheiro `.env` deve conter as variáveis `API_KEY` e `PORT`, exemplo:
```
API_KEY=<your_imdb_api_key>
PORT=8080
```
Utilizamos este método para não expor keys privadas.

## Problemas que podem acontecer
- Caso as rotas dos filmes estejam a retornar body vazio/sem filmes, o problema poderá ser da API key já ter esgotado os 100 pedidos.
- Erro de ECONNREFUSED ou algo do género pode ser por o ElasticSearch não estar a correr, aconselhado ler [#setup](#setup)

### Trabalho realizado por
- Roberto Petrisoru (a49418)
- Ruben Louro (a48926)
