package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=%s&apikey=%s";
    private final String API_KEY = "59b6e197";

    public void exibiMenu() {
        System.out.printf("Digite o nome da série ou filme que deseja pesquisar: ");

        var nomeSerie = leitura.nextLine();
        var nomeFormatado = nomeSerie.replace(" ", "+");


        var url = String.format(ENDERECO, nomeFormatado, API_KEY);

        var json = consumo.obterDados(url);

        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

         List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(String.format(ENDERECO + "&Season=%d", nomeFormatado, API_KEY, i));
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

       for (int i = 0; i < dados.totalTemporadas(); i ++){
            List<DadosEpisodios> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++){
                System.out.printf("Temporada %d - Episódio %d: %s%n", (i + 1), (j + 1), episodiosTemporada.get(j).titulo());
            }
        }
       List<DadosEpisodios> dadosEpisodios = temporadas.stream()
               .flatMap(t -> t.episodios().stream())
               .collect(Collectors.toList());
        System.out.printf("\nTop 5 episódios com as melhores avaliações:%n");
       dadosEpisodios.stream()
               .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
               .sorted(Comparator.comparing(DadosEpisodios::avaliacao).reversed())
        .limit(5)
               .forEach(System.out::println);

       List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

       episodios.forEach(System.out::println);

        System.out.printf("Digite um trecho do título do episódio que deseja buscar: ");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.printf("Episódio encontrado: %d%n ", episodioBuscado.get().getTemporada());
        } else {
            System.out.println("Nenhum episódio encontrado.");
        }

        System.out.printf("A partir de qual data deseja ver os episódios lançados?");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.printf("Temporada %d - Episódio %d: %s (Lançado em: %s)%n",
                        e.getTemporada(),
                        e.getNumeroEpisodio(),
                        e.getTitulo(),
                        e.getDataLancamento().format(formatador)));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(
                        Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Total de episódios avaliados: " + est.getCount());
    }
}
