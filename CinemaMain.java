package venda_de_ingressos_de_cinema;

import java.io.*;
import java.util.Scanner;

public class CinemaMain {

    static Scanner scan = new Scanner(System.in);
    static StringBuffer memoriaFilmes = new StringBuffer();
    static StringBuffer memoriaIngressos = new StringBuffer();

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("ATENCAO: NAO use acentos ou caracteres especiais.");
        System.out.println("Exemplo: acao, comedia, suspenso, ficcao.");
        System.out.println("nome dos integrantes do grupo:\n" +
                            "Bryan Monteiro Nascimento,\n" +
                            "Davi Pitangui,\n" +
                            "Walisson Pastori Campos");
        System.out.println("==============================================\n");

        iniciarArquivoFilmes();
        iniciarArquivoIngressos();

        int op = -1;
        while (op != 0) {
            mostrarMenu();
            String entrada = scan.nextLine();
            Integer escolha = parseIntSafe(entrada);
            if (escolha == null) {
                System.out.println("Opcao invalida. Digite um numero.");
                continue;
            }
            op = escolha;
            switch (op) {
                case 1:
                    cadastrarFilmeGuided();
                    break;
                case 2:
                    cadastrarIngressoGuided();
                    break;
                case 3:
                    alterarIngressoGuided();
                    break;
                case 4:
                    excluirIngressoGuided();
                    break;
                case 5:
                    consultarFilmes();
                    break;
                case 6:
                    consultarIngressos();
                    break;
                case 7:
                    consultaEspecificaGuided();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opcao invalida!");
            }
        }
    }

    static void mostrarMenu() {
        System.out.println("\n======== MENU CINEMA ========");
        System.out.println("1 - Cadastrar Filme");
        System.out.println("2 - Cadastrar Ingresso");
        System.out.println("3 - Alterar Ingresso");
        System.out.println("4 - Excluir Ingresso");
        System.out.println("5 - Consultar Filmes");
        System.out.println("6 - Consultar Ingressos");
        System.out.println("7 - Consulta especifica (Filme -> Ingressos)");
        System.out.println("0 - Sair");
        System.out.println("==============================");
        System.out.print("Opcao: ");
    }

    static void cadastrarFilmeGuided() {
        System.out.println("\n=== CADASTRAR FILME ===");

        int id;
        while (true) {
            System.out.print("ID do filme (numero inteiro positivo) ou C para cancelar: ");
            String entrada = scan.nextLine().trim();
            if (entrada.equalsIgnoreCase("C")) {
                System.out.println("Cadastro cancelado.");
                return;
            }
            Integer tmp = parseIntSafe(entrada);
            if (tmp == null || tmp <= 0) {
                System.out.println("Valor invalido. Digite um numero inteiro positivo.");
                continue;
            }
            if (filmeExiste(tmp)) {
                System.out.println("ERRO: ja existe um filme com esse ID. Digite outro ID ou C para cancelar.");
                continue;
            }
            id = tmp;
            break;
        }

        String nome = lerStringNaoVazia("Nome do filme: ");
        String genero = lerStringNaoVazia("Genero: ");
        int duracao = lerIntPositivo("Duracao (minutos, inteiro > 0): ");

        System.out.printf("\nConfirmar cadastro: ID=%d | Nome=%s | Genero=%s | Duracao=%d min\n", id, nome, genero,
                duracao);
        if (!confirmar("Confirmar")) {
            System.out.println("Cadastro cancelado.");
            return;
        }else{  

        Filme f = new Filme(id, nome, genero, duracao);
        memoriaFilmes.append(f.linhaTXT());
        gravarArquivoFilmes();
        System.out.println("Filme cadastrado com sucesso!");
    }
}

    static void cadastrarIngressoGuided() {
        if (memoriaFilmes.length() == 0) {
            System.out.println("Nao ha filmes cadastrados. Cadastre um filme antes de criar ingressos.");
            return;
        }

        System.out.println("\n===== FILMES DISPONIVEIS =====");
        System.out.printf("| %-5s | %-30s | %-12s | %-8s |\n", "ID", "Nome", "Genero", "Duracao");
        String[] linhasF = memoriaFilmes.toString().split("\n");
        for (String linha : linhasF) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            if (dados.length < 4)
                continue;
            System.out.printf("| %-5s | %-30s | %-12s | %-8s |\n", dados[0], dados[1], dados[2], dados[3]);
        }
        System.out.println("=====================================\n");

        int idIngresso;
        while (true) {
            idIngresso = lerIntPositivo("ID do ingresso (numero inteiro positivo): ");
            if (ingressoExiste(idIngresso)) {
                System.out.println("ERRO: ja existe um ingresso com esse ID. Tente outro.");
                continue;
            }
            break;
        }

        int idFilme;
        while (true) {
            idFilme = lerIntPositivo("ID do filme (numero): ");
            if (!filmeExiste(idFilme)) {
                System.out.println("ERRO: Filme com esse ID nao existe. Tente novamente.");
                continue;
            }
            break;
        }

        String horario = lerHorario("Horario (HH:mm): ");
        double preco = lerDoublePositivo("Preco (ex: 25.00): ");

        System.out.printf("\nConfirmar cadastro: IDIngresso=%d | IDFilme=%d (%s) | Horario=%s | Preco=%.2f\n",
                idIngresso, idFilme, getNomeFilme(idFilme), horario, preco);
        if (!confirmar("Confirmar")) {
            System.out.println("Cadastro cancelado.");
            return;
        }

        Ingresso ing = new Ingresso(idIngresso, idFilme, horario, preco);
        memoriaIngressos.append(ing.linhaTXT());
        gravarArquivoIngressos();
        System.out.println("Ingresso cadastrado com sucesso!");
    }

    static void alterarIngressoGuided() {
        if (memoriaIngressos.length() == 0) {
            System.out.println("Nao ha ingressos cadastrados.");
            return;
        }

        consultarIngressos();
        System.out.println("=====================================\n");

        int id = lerIntPositivo("Digite o ID do ingresso a alterar: ");
        if (!ingressoExiste(id)) {
            System.out.println("Ingresso nao encontrado. Operacao cancelada.");
            return;
        }

        String[] linhas = memoriaIngressos.toString().split("\n");
        StringBuffer novoBuffer = new StringBuffer();
        boolean achou = false;

        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;

            if (linha.startsWith(id + "\t")) {
                achou = true;
                String[] dados = linha.split("\t");
                if (dados.length < 4) {
                    novoBuffer.append(linha).append("\n");
                    continue;
                }

                System.out.printf("Ingresso selecionado: ID=%s | Filme=%s (%s) | Horario=%s | Preco=%s\n",
                        dados[0], dados[1], getNomeFilme(parseIntSafe(dados[1]) == null ? -1 : parseIntSafe(dados[1])),
                        dados[2], dados[3]);

                String horario = lerHorario("Novo horario (HH:mm): ");
                double preco = lerDoublePositivo("Novo preco: ");

                String novaLinha = dados[0] + "\t" + dados[1] + "\t" + horario + "\t" + preco;
                novoBuffer.append(novaLinha).append("\n");
            } else {
                novoBuffer.append(linha).append("\n");
            }
        }

        if (!achou) {
            System.out.println("Ingresso nao encontrado.");
            return;
        }

        if (!confirmar("Confirmar alteracao?")) {
            System.out.println("Alteracao cancelada.");
            return;
        }

        memoriaIngressos = novoBuffer;
        gravarArquivoIngressos();
        System.out.println("Ingresso alterado com sucesso.");
    }

    static void excluirIngressoGuided() {
        if (memoriaIngressos.length() == 0) {
            System.out.println("Nao ha ingressos cadastrados.");
            return;
        }

        consultarIngressos();
        System.out.println("=====================================\n");

        int id = lerIntPositivo("Digite o ID do ingresso a excluir: ");
        if (!ingressoExiste(id)) {
            System.out.println("Ingresso nao encontrado. Operacao cancelada.");
            return;
        }

        System.out.println("Ingresso selecionado:");
        String[] linhas = memoriaIngressos.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            if (linha.startsWith(id + "\t")) {
                String[] d = linha.split("\t");
                System.out.printf("ID=%s | Filme=%s (%s) | Horario=%s | Preco=%s\n",
                        d[0], d[1], getNomeFilme(parseIntSafe(d[1]) == null ? -1 : parseIntSafe(d[1])), d[2], d[3]);
                break;
            }
        }

        if (!confirmar("Deseja realmente excluir este ingresso?")) {
            System.out.println("Exclusao cancelada.");
            return;
        }

        StringBuffer novoBuffer = new StringBuffer();
        boolean achou = false;
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            if (linha.startsWith(id + "\t")) {
                achou = true;
                continue;
            }
            novoBuffer.append(linha).append("\n");
        }

        if (!achou) {
            System.out.println("Ingresso nao encontrado.");
            return;
        }

        memoriaIngressos = novoBuffer;
        gravarArquivoIngressos();
        System.out.println("Ingresso excluido com sucesso.");
    }

    static void consultarFilmes() {
        System.out.println("\n===== LISTA DE FILMES =====");
        System.out.printf("| %-5s | %-30s | %-15s | %-8s |\n", "ID", "Nome", "Genero", "Duracao");

        String[] linhas = memoriaFilmes.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            if (dados.length < 4)
                continue;
            System.out.printf("| %-5s | %-30s | %-15s | %-8s |\n", dados[0], dados[1], dados[2], dados[3]);
        }
    }

    static void consultarIngressos() {
        System.out.println("\n===== LISTA DE INGRESSOS =====");
        System.out.printf("| %-5s | %-6s | %-30s | %-8s | %-8s |\n", "ID", "Filme", "Nome do Filme", "Horario",
                "Preco");

        String[] linhas = memoriaIngressos.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;

            String[] dados = linha.split("\t");
            if (dados.length < 4)
                continue;

            Integer idFilme = parseIntSafe(dados[1]);
            int idF = (idFilme == null) ? -1 : idFilme;

            System.out.printf("| %-5s | %-6s | %-30s | %-8s | %-8s |\n",
                    dados[0], dados[1], getNomeFilme(idF), dados[2], dados[3]);
        }
    }

    static void consultaEspecificaGuided() {
        if (memoriaFilmes.length() == 0) {
            System.out.println("Nao ha filmes cadastrados.");
            return;
        }

        int idFilme;
        while (true) {
            idFilme = lerIntPositivo("Digite o ID do filme para consulta: ");
            if (!filmeExiste(idFilme)) {
                System.out.println("Filme nao encontrado. Tente novamente.");
                continue;
            }
            break;
        }

        String[] linhasF = memoriaFilmes.toString().split("\n");
        String nome = "", genero = "", dur = "";
        for (String linha : linhasF) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            if (dados.length < 4)
                continue;
            Integer id = parseIntSafe(dados[0]);
            if (id != null && id == idFilme) {
                nome = dados[1];
                genero = dados[2];
                dur = dados[3];
                break;
            }
        }

        System.out.println("\nFILME ENCONTRADO:");
        System.out.printf("ID: %d | Nome: %s | Genero: %s | Duracao: %s min\n", idFilme, nome, genero, dur);

        System.out.println("\nIngressos deste filme:");
        System.out.println("--------------------------------------------");
        System.out.printf("| %-5s | %-10s | %-8s |\n", "ID", "Horario", "Preco");
        System.out.println("--------------------------------------------");

        boolean achouIngressos = false;
        String[] linhasIng = memoriaIngressos.toString().split("\n");
        for (String linha : linhasIng) {
            if (linha.trim().isEmpty())
                continue;

            String[] dados = linha.split("\t");
            if (dados.length < 4)
                continue;

            Integer idFilIng = parseIntSafe(dados[1]);
            if (idFilIng != null && idFilIng == idFilme) {
                System.out.printf("| %-5s | %-10s | %-8s |\n", dados[0], dados[2], dados[3]);
                achouIngressos = true;
            }
        }

        System.out.println("--------------------------------------------");
        if (!achouIngressos)
            System.out.println("Nenhum ingresso para este filme.");
    }

    static int lerIntPositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scan.nextLine();
            Integer v = parseIntSafe(s);
            if (v != null && v > 0)
                return v;
            System.out.println("Valor invalido. Deve ser numero inteiro positivo.");
        }
    }

    static int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scan.nextLine();
            Integer v = parseIntSafe(s);
            if (v != null)
                return v;
            System.out.println("Valor invalido. Digite um numero inteiro.");
        }
    }

    static double lerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scan.nextLine();
            Double d = parseDoubleSafe(s);
            if (d != null && d > 0)
                return d;
            System.out.println("Valor invalido. Tente novamente.");
        }
    }

    static String lerStringNaoVazia(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scan.nextLine().trim();
            if (!s.isEmpty())
                return s;
            System.out.println("Entrada vazia. Tente novamente.");
        }
    }

    static String lerHorario(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scan.nextLine().trim();
            if (validarHorario(s))
                return s;
            System.out.println("Horario invalido. Formato HH:mm.");
        }
    }

    static boolean confirmar(String prompt) {
        while (true) {
            System.out.print(prompt + " (S/N): ");
            String s = scan.nextLine().trim().toUpperCase();
            if (s.equals("S") || s.equals("SIM"))
                return true;
            if (s.equals("N") || s.equals("NAO"))
                return false;
        }
    }

    static Integer parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    static Double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    static boolean validarHorario(String horario) {
        return horario.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    static boolean filmeExiste(int idFilme) {
        String[] linhas = memoriaFilmes.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            Integer id = parseIntSafe(dados[0]);
            if (id != null && id == idFilme)
                return true;
        }
        return false;
    }

    static boolean ingressoExiste(int idIngresso) {
        String[] linhas = memoriaIngressos.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            Integer id = parseIntSafe(dados[0]);
            if (id != null && id == idIngresso)
                return true;
        }
        return false;
    }

    static String getNomeFilme(int idFilme) {
        String[] linhas = memoriaFilmes.toString().split("\n");
        for (String linha : linhas) {
            if (linha.trim().isEmpty())
                continue;
            String[] dados = linha.split("\t");
            Integer id = parseIntSafe(dados[0]);
            if (id != null && id == idFilme)
                return dados[1];
        }
        return "N/A";
    }

    static void iniciarArquivoFilmes() {
        memoriaFilmes.delete(0, memoriaFilmes.length());
        File f = new File("Filmes.txt");
        if (!f.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                memoriaFilmes.append(linha).append("\n");
            }
        } catch (Exception e) {
        }
    }

    static void gravarArquivoFilmes() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Filmes.txt"))) {
            bw.write(memoriaFilmes.toString());
        } catch (Exception e) {
        }
    }

    static void iniciarArquivoIngressos() {
        memoriaIngressos.delete(0, memoriaIngressos.length());
        File f = new File("Ingressos.txt");
        if (!f.exists())
            return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                memoriaIngressos.append(linha).append("\n");
            }
        } catch (Exception e) {
        }
    }

    static void gravarArquivoIngressos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Ingressos.txt"))) {
            bw.write(memoriaIngressos.toString());
        } catch (Exception e) {
        }
    }
}
