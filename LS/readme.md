# 2223-2-common
Software Laboratory, 2022/2023, Spring semester

# Environment variables (.env)
- `DB_NAME`: The name of the PostgreSQL database.
- `DB_USER`: The username used to connect to the PostgreSQL database.
- `DB_PW`: The password used to connect to the PostgreSQL database.
- `DB_HOST`: The hostname of the PostgreSQL server.
- `PORT` : Server port.
- `SERVER_STORAGE` : Where to store data (`LOCAL` (in memory) or `REMOTE` (remote PSQL database)).
- `API`: For development use `DEV`, for live production use `PROD`. 

*If using `REMOTE` for `SERVER_STORAGE`, make sure to set the database variables correct.*

*If using `DEV` for `API`, database nukes can happen, so use this setting carefully.**

Check [.env.example](.env.example) for an example configuration.

# Docker
For learning & functionality purposes, I have decided to use Docker for this project. It's use is for the PostgreSQL database. Everything is described inside [docker-compose.yml](./docker-compose.yml).
## Start docker and container
```sh
sudo systemctl start docker
sudo docker-compose up -d
```

## Connect to database
```sh
psql -h localhost -U myuser -d tasks
```

## Stop container
```sh
sudo docker-compose down
```

# Ktlint
[ktlint.sh](./ktlint.sh) is a util script for running Ktlint gradle tasks to lint and format the project source code. The script should be executed before pushing to github so Jenkins actions pass. **Some rules can't be auto-corrected by `ktlint` so make sure to read the output errors if any!**

# Students
* Roberto Petrisoru (a49418@alunos.isel.pt)
