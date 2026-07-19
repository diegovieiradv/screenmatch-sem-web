# Screenmatch (CLI)

Aplicacao console para busca e analise de series de TV usando a OMDb API.

## Descricao

Projeto de linha de comando que consome a OMDb API para buscar dados de series, exibir informacoes detalhadas e permitir busca por episodios.

## Funcionalidades

- Busca de series por nome na OMDb API
- Exibicao de dados (titulo, temporadas, avaliacao)
- Listagem de episodios por temporada
- Top 5 episodios melhor avaliados
- Busca por titulo de episodio
- Analise de notas por temporada

## Tecnologias

- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.5.8 (CommandLineRunner)
- **HTTP Client:** `java.net.http.HttpClient`
- **JSON:** Jackson Databind
- **API:** OMDb API
- **Build:** Maven

## Como Rodar

```bash
# Execute
./mvnw spring-boot:run

# Ou build
./mvnw clean package
java -jar target/screenmatch-0.0.1-SNAPSHOT.jar
```

**Nota:** Necessita conexao com internet para acessar a OMDb API.

## Estrutura

```
src/main/java/.../
├── ScreenmatchApplication.java  # Entry point
├── principal/
│   └── Principal.java           # Menu e logica
├── model/
│   ├── DadosSerie.java          # Record: dados da serie
│   ├── DadosTemporada.java      # Record: dados da temporada
│   ├── DadosEpisodios.java      # Record: dados do episodio
│   └── Episodio.java            # Objeto rico de episodio
└── service/
    ├── ConsumoApi.java          # HTTP client
    └── ConverteDados.java       # JSON para objeto
```

## Licenca

MIT License - Diego Vieira
