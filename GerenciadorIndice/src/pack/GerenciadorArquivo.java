
package pack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pack.GerenciadorArquivo.buscarPalavra;


public class GerenciadorArquivo {
    //Palavras desconsideradas
    Set<String> palavrasDesconsideradas = new HashSet<>(Arrays.asList(",", ".", "!", "?", ""));
    public List<String> lerArquivoConsulta(String arquivoConsulta) {
        List<String> nomesArquivos = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoConsulta))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                nomesArquivos.add(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nomesArquivos;
    }
    
    public static void main(String[] args) {
        
        GerenciadorArquivo gerenciando = new GerenciadorArquivo();
        String diretorioRaiz = System.getProperty("user.dir");
        String arquivoConsulta = diretorioRaiz + "/src/pack/" + "consulta.txt";
        
        List<String> nomesArquivos = gerenciando.lerArquivoConsulta(arquivoConsulta);
        
        //Camimnhos do arquivos de dados raiz
        String arquivo1 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(0);
        String arquivo2 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(1);
        String arquivo3 = diretorioRaiz + "/src/pack/" + nomesArquivos.get(2);
         
        //Caminhos das palvras a ser descosideradas.
        String arquivo4 = diretorioRaiz + "/src/pack/" + "desconsideradas.txt";
        
        //listando todas as palavrasDesonsideradas em set
        gerenciando.ListaParaDesconsiderar(arquivo4);
    
        //Filtrar as palavras em um array list
        List<String> a = filtrarPalavras(arquivo1, gerenciando.palavrasDesconsideradas);
        List<String> b = filtrarPalavras(arquivo2, gerenciando.palavrasDesconsideradas);
        List<String> c = filtrarPalavras(arquivo3, gerenciando.palavrasDesconsideradas);
        
        /*System.out.println("Arquivo A: " + a);
        System.out.println("Arquivo B: " + b);
        System.out.println("Arquivo C: " + c);*/
        
       
        //remove todas as palvras repetidas usando o Set<String>
        Set<String> palavrasSet = new HashSet<>();
        palavrasSet.addAll(a);
        palavrasSet.addAll(b);
        palavrasSet.addAll(c);
       
        //Converter para usar o metodo de ordenação
        List<String> Dicionario = new ArrayList<>(palavrasSet);
        Collections.sort(Dicionario);
        
        //Gerando indice investido
        List<Palavra> dicionario=  buscarPalavra(a, b, c, Dicionario);
        listarResultados(dicionario);
        
        GerarArquivoIndex("index.txt", dicionario);
        //pegar o nome a ser buscado
       
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite uma palavra: ");
        String palavra = scanner.nextLine();
        
        //Buscar dados da list de objetos
        Palavra palavraEncontrada = buscarPalavraPorNome(dicionario, palavra);
        //imprimir caminhos
        gerenciando.imprimirCaminhos(palavraEncontrada);
       
    }    
    
    public void imprimirCaminhos(Palavra palavraEncontrada){
        palavraEncontrada.getCaminhos();
        if (palavraEncontrada != null) {
            for (Iterator<String> it = palavraEncontrada.getCaminhos().iterator(); it.hasNext();) {
                String arquivo = it.next();
                System.out.println(arquivo);
            }

        } else {
            System.out.println("Palavra não encontrada.");
        }
    }
  
     public static Palavra buscarPalavraPorNome(List<Palavra> resultados, String palavra) {
        for (Palavra resultado : resultados) {
            if (resultado.getNome().equals(palavra)) {
                return resultado;
            }
        }
        return null;
    }
    public static void GerarArquivoIndex(String nomeArquivo, List<Palavra> resultados) {
        String diretorioRaiz = System.getProperty("user.dir");
        String caminhoCompleto = diretorioRaiz + "/src/pack/" + nomeArquivo;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoCompleto))) {
            for (Palavra resultado : resultados) {
                List<String> caminhos = resultado.getCaminhos();
                writer.write(resultado.getNome() + ":");
                List<Integer> quantidade = resultado.getRepetidas();
                int x = 0;
                for (String arquivo : caminhos) {
                    if (arquivo.equals("a.txt")) {
                        writer.write(" 1," + quantidade.get(x));
                    }
                    if (arquivo.equals("b.txt")) {
                        writer.write(" 2," + quantidade.get(x));
                    }
                    if (arquivo.equals("c.txt")) {
                        writer.write(" 3," + quantidade.get(x));
                    }
                    x++;
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void listarResultados(List<Palavra> resultados) {
        
        for (Palavra resultado : resultados) {
            List<String> caminhos = resultado.getCaminhos();
            System.out.print(resultado.getNome()+ ":");
            List<Integer> quantidade = resultado.getRepetidas();
            int x = 0;
            for(String arquivo: caminhos){
                if(arquivo =="a.txt"){
                    System.out.print(" 1," + quantidade.get(x));
                }
                if(arquivo =="b.txt"){
                     System.out.print(" 2,"+ quantidade.get(x));
                }
                if(arquivo == "c.txt"){
                     System.out.print(" 3," + quantidade.get(x));
                }
                x++;
            }
            System.out.println();
  
        }
        
    }
    
    public static List<Palavra> buscarPalavra(List<String> listaA, List<String> listaB, List<String> listaC, List<String> ListaPalavras) {
        List<Palavra> resultados = new ArrayList<>();
       
        for(String palavra:ListaPalavras ){
            List<String> caminhos =new ArrayList<>();
            List<Integer> quantidade =new ArrayList<>();
          
            int quantidadeRepeticoesListaA = contarRepeticoes(listaA, palavra);
            int quantidadeRepeticoesListaB = contarRepeticoes(listaB, palavra);
            int quantidadeRepeticoesListaC = contarRepeticoes(listaC, palavra);
         
            if(quantidadeRepeticoesListaA > 0){
                caminhos.add("a.txt");
                quantidade.add(quantidadeRepeticoesListaA);
                
            }
            if(quantidadeRepeticoesListaB > 0){
                caminhos.add("b.txt");
                quantidade.add(quantidadeRepeticoesListaB);
            }
            if(quantidadeRepeticoesListaC > 0){
                caminhos.add("c.txt");
                quantidade.add(quantidadeRepeticoesListaC);
            }
            resultados.add(new Palavra(palavra, quantidade, caminhos));
            
           
               
        }   

        return resultados;
    }
    
    public static int contarRepeticoes(List<String> lista, String palavra) {
        int contador = 0;
        for (String elemento : lista) {
            if (elemento.equals(palavra)) {
                contador++;
            }
        }
        return contador;
    }
   
    public void ListaParaDesconsiderar(String arquivo){
        
       
        try {
            //Lendo o arquivo com as palavras desconsideradas
            Scanner scannerArquivo = new Scanner(new File(arquivo));
            String palavra;
            
            //Enquanto houver palavras disponíveis
            while(scannerArquivo.hasNext()){
                palavra = scannerArquivo.next().replaceAll("[,;!\\.\\?' ']", " ");
                palavrasDesconsideradas.add(palavra.toLowerCase());//toLowerCase trona as palavras menusculas
            }
            
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GerenciadorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
           //Não precisa de retorno pois teemos uma variavel global
           
    }

    public static List<String> filtrarPalavras(String arquivo, Set<String> palavrasDesconsideradas) {
    List<String> palavrasFiltradas = new ArrayList<>();//Array list para pegar as palavras repetidas

    try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
        String linha;

        while ((linha = reader.readLine()) != null) {
            String[] palavras = linha.split(" ");

            for (String palavra : palavras) {
                palavra = palavra.trim().toLowerCase().replaceAll("[.,;!]", ""); // Remove pontuações do início e do final da palavra
                palavra = palavra.replaceAll("[^a-zA-Z0-9]", ""); // Remove pontuações internas na palavra

                if (!palavrasDesconsideradas.contains(palavra)) {
                    palavrasFiltradas.add(palavra);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

        return palavrasFiltradas;
    }
    
}
