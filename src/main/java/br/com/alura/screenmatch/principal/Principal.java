package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodios;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
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
    }
}
