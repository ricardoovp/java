/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp_sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author tiago
 */
public class Parser {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Introduza a Query:");
        Scanner S = new Scanner(System.in);
        String query = S.nextLine();
        endQuery(query);
        sql_to_Java(query);
    }

    public static void sql_to_Java(String query) throws FileNotFoundException {

        //INSERT INTO tabela(id, nome, idade) VALUES(1, qwe, 20);
        //SELECT * FROM tabela WHERE id=4;
        //UPDATE tabela SET id= 4, nome =qwe WHERE id=3;
        //UPDATE tabela SET id= 4, nome =qwe, idade = 19;
        //DELETE FROM tabela WHERE id=6;
        //DELETE FROM tabela;
        query = query.toLowerCase(); // Torna tudo case-insensitive. Lê independentemente se é maiúsculo ou minúsculo
        int flag = 0;

//*******************************************************************************************************************************************************************
// COMANDO SELECT
//*******************************************************************************************************************************************************************
        if (query.startsWith("select ")) { // se comecar com o comando SELECT
            query = query.replaceFirst("select ", ""); // subsitui o select por nada. Ou seja, remove o select
            //System.out.println(query); // string sem "select "

            flag = 1; //flag ativa quando é lida a operação a realizar

            int ind_from = query.indexOf(" from"); // obtém o indice do " from"
            String campos_str = query.substring(0, ind_from); // nova string que contem os campos (desde o inicio até ao indice do from, ou seja, os campos). Vai retirar da string anterior "query" o que se encontra na posição zero até a posição ind_from (indice do " from).
            //System.out.println(campos_str); //imprime os campos da string. Se o campo for "*", imprime só o "*"
            query = query.substring(ind_from + 1); // remove os campos, deixando o resto da query
            //System.out.println(query); // string sem os campos

            //System.out.println(query); // string sem os campos
            String[] campos = campos_str.split(","); // Guarda os campos separados por virgula num array

            for (int i = 0; i < campos.length; i++) {
                campos[i] = campos[i].trim(); // remove os espacos em volta dos elementos
            }

            if (!query.startsWith("from ")) {
                // ERRO (excecao)
            }

            query = query.replaceFirst("from ", ""); // remove o from
            int ind_esp = query.indexOf(" "); // indice do proximo espaco. Neste caso, o espaco depois do nome da tabela.
            if (ind_esp == -1) { // se nao existir
                ind_esp = query.length() - 1; // le ate ao fim, ou seja, so existe "from tabela;" e le ate esse fim
            }

            String tabela = query.substring(0, ind_esp); // obtem o nome da tabela que se encontra na posicao zero até indice do prox espaco
            //System.out.println(tabela); //mostra tabela

            query = query.substring(tabela.length()); // OU query.substring(ind_esp + 1); remove o nome da tabela na string query
            // e a query fica: " WHERE id=4;"
            query = query.trim(); ////remove o espaço que se encontra antes do WHERE: " WHERE..." e fica "WHERE" (se houver where)

            //System.out.println(query); // e a query fica: "WHERE id=4;"
            //System.out.println(query); //caso nao haja a condicao WHERE, só mostra o ";"
            where(query, tabela, campos, flag);
        } //************************************************************************************************************************************************************************
        // COMANDO INSERT
        //************************************************************************************************************************************************************************
        else if (query.startsWith("insert into ")) {
            query = query.replaceFirst("insert into", ""); // Remover INSERT INTO do inicio da query
            //System.out.println(query); //string sem "insert into "
            int ind_values = query.indexOf(" values");
            int ind_abrirparentesis = query.indexOf("("); // obter o indice/posicao do abre parentises
            int menor_ind = ind_abrirparentesis;
            if (ind_values < menor_ind) {
                menor_ind = ind_values;
            }
            String tabela = query.substring(0, menor_ind); // Obtem o nome da tabela
            query = query.substring(tabela.length());
            //System.out.println(query); //string sem "tabela"
            String[] campos = {};
            String[] valores = {};
            if (query.startsWith("(")) {
                // Tipo 1 - INSERT INTO tabela(id, nome, idade) VALUES(1, qwe, 20);
                int ind_fecharparentesis = query.indexOf(")"); // Obter a posicao de )
                String campos_str = query.substring(1, ind_fecharparentesis); // Obter os campos dentro dos primeiros parentesis
                campos = campos_str.split(","); // Separar os campos pelas virgulas e guardar no array campos
                tabela = tabela.trim();
                for (int i = 0; i < campos.length; i++) {
                    System.out.println("Campo " + (i + 1) + ": " + campos[i].trim());
                }

                //System.out.println(campos_str);// so os campos
                query = query.substring(campos_str.length() + 2); // Remover os campos da query. Fica " VALUES(1, qwe, 20);"
                query = query.trim(); // agora fica "VALUES(1, qwe, 20);"
                query = query.replaceFirst("values", ""); // Remover VALUES do inicio da linha
                tabela = tabela.trim();
                System.out.println("\t");

                int ind_fecharparentesisv = query.indexOf(")"); // Obter a posicao de )
                String valores_str = query.substring(1, ind_fecharparentesisv); // Obter os campos dentro dos primeiros parentesis
                valores = valores_str.split(","); // Separar os campos pelas virgulas e guardar no array campos

                for (int i = 0; i < valores.length; i++) {
                    System.out.println("Valor " + (i + 1) + ": " + valores[i].trim());

                }

                try (PrintWriter pw = new PrintWriter(new File("pessoas.csv"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(tabela + ";\n\n");
                    sb.append(campos_str + ";\n");
                    sb.append(valores_str + ";\n");
                    pw.write(sb.toString());
                }
            }
            // Tipo 2 - insert into tabela values(valor1, valor2, valor3);
            query = query.trim();
            if (!query.startsWith("values")) {
                // Mandar erro: falta VALUES, e terminar o programa
            }
            if (query.startsWith("values")) {

                query = query.replaceFirst("values", ""); // Remover VALUES do inicio da linha
                if (query.startsWith("(")) {
                    int ind_fecharparentesis = query.indexOf(")"); // Obter a posicao de )
                    String valores_str = query.substring(1, ind_fecharparentesis); // Obter os campos dentro dos primeiros parentesis
                    valores = valores_str.split(","); // Separar os campos pelas virgulas e guardar no array campos

                    //System.out.println(query); //valores inseridos na tabela. Fica "1, qwe, 20"
                    System.out.println("");
                    tabela = tabela.trim();

                    for (int i = 0; i < valores.length; i++) {
                        System.out.println("Valor " + (i + 1) + ": " + valores[i].trim());

                    }

                    try (PrintWriter pw = new PrintWriter(new File("pessoas.csv"))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(tabela + ";\n\n");
                        sb.append(valores_str + ";\n");
                        pw.write(sb.toString());
                    }
                }
            }
            if (campos.length == 0) { // se a query nao definir os campos
                //insert_2(tabela, valores);
            } else { // se a query definir os campos
                //insert_1(tabela, campos, valores);
            }

        } //***************************************************************************************************************************************************************************
        //UPDATE
        //***************************************************************************************************************************************************************************        
        else if (query.startsWith("update ")) {// Se a query comecar com o texto UPDATE
            query = query.replaceFirst("update ", ""); // Remove o UPDATE
            //System.out.println(query); // string sem "update "

            int ind_esp = query.indexOf(" "); // Encontra a posicao do proximo espaco
            if (ind_esp == -1) {
                ind_esp = query.length() - 1; // Se nao encontrar um espaco, entao ler ate ao fim
            }

            String tabela = query.substring(0, ind_esp); // Obtem o nome da tabela, lendo do inicio da string ate ao espaco
            //System.out.println(tabela);//mostra o nome da tebela

            query = query.substring(tabela.length()); // Remove o nome da tabela do inicio da query
            query = query.trim();
            //System.out.println(query); //string sem o nome da tabela

            if (!query.startsWith("set ")) {
                // Mandar erro, pq FROM não foi encontrado e terminar programa
            }

            query = query.replaceFirst("set ", ""); // Remove o SET
            //System.out.println(query); // string sem "set "
            query = query.trim();

            ArrayList<Integer> aux = new ArrayList<>();

            int index = query.indexOf('=');
            while (index != -1) { //enquanto restarem indices do '=' ele vai busca-los
                aux.add(index);
                //System.out.println(index); //imprime indices dos "="
                index = query.indexOf('=', index + 1);
                // e adiciona-os ao aux              
            }

            ArrayList<String> aux_campo = new ArrayList<>();
            ArrayList<String> aux_valor = new ArrayList<>();

            for (int i = 0; i < aux.size(); i++) {
                String valor = "";

                int le_index = aux.get(i) + 1;
                if (query.charAt(le_index) == ' ') {
                    le_index++;
                }
                while (query.charAt(le_index) != ' ' && query.charAt(le_index) != ',' && query.length() > le_index + 1) {

                    valor += query.charAt(le_index);//concactenar o que esta no index
                    le_index++;
                }
                valor = valor.trim();

                String campo = "";
                int le_other_index = aux.get(i);
                if (query.charAt(le_other_index - 1) == ' ') {
                    campo = "" + query.charAt(le_other_index - 2);
                    le_other_index -= 2;
                }
                while (query.charAt(le_other_index) != ' ' && query.charAt(le_other_index) != ',' && le_other_index > 0) {
                    le_other_index--;
                    campo = query.charAt(le_other_index) + campo;//concactenar o que esta no index

                }
                campo = campo.trim();

                System.out.println(campo + " : " + valor);

                try (PrintWriter pw = new PrintWriter(new File("pessoas.csv"))) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(tabela + ";\n\n");
                    sb.append(aux_campo + ";\n");
                    sb.append(aux_valor + ";\n");
                    pw.write(sb.toString());
                }

                //meter a guardar num array
                aux_campo.add(campo);
                aux_valor.add(valor);
            }
            //System.out.println(query);

            int ind_where = query.indexOf("where"); // Encontra a posicao do where
            if (ind_where == -1) {//se nao encontrar o "where" e pq n tem condicao logo altera os campos todos
                //chama public static void update_2() SEM CONDICAO
                System.out.println("Campos a Alterar: (sem condicao)");
                System.out.println(aux_campo);
                System.out.println(aux_valor);
            } else {
                String campo_cond = aux_campo.get(aux_campo.size() - 1);//guarda o campo da condicao
                System.out.println("Campo da Condicao: " + campo_cond);

                String valor_cond = aux_valor.get(aux_valor.size() - 1);//guarda o valor da condicao
                System.out.println("Valor da Condicao: " + valor_cond);

                //remove o ultimo elemento de cada arraylist pois esses sao os da condicao
                System.out.println("Campos a Alterar: (com condicao)");
                aux_campo.remove(aux_campo.size() - 1);
                System.out.println(aux_campo);
                aux_valor.remove(aux_valor.size() - 1);
                System.out.println(aux_valor);
                //chama public static void update_1() COM CONDICAO
            }
        } //***************************************************************************************************************************************************************************
        //DELETE
        //***************************************************************************************************************************************************************************
        else if (query.startsWith("delete ")) {
            query = query.replaceFirst("delete ", ""); // Remove o DELETE
            //System.out.println(query); // string sem "delete "

            flag = 2;

            if (!query.startsWith("from ")) {
                // Mandar erro, pq FROM nao foi encontrado e terminar programa
            }
            query = query.replaceFirst("from ", "");
            //System.out.println(query);

            query = query.replaceFirst("from ", ""); // Remove o FROM
            int ind_esp = query.indexOf(" "); // Encontra a posico do proximo espaco
            if (ind_esp == -1) {
                ind_esp = query.length() - 1; // Se nao encontrar um espaco, entao ler ate ao fim
            }

            String tabela = query.substring(0, ind_esp); // Obtem o nome da tabela, lendo do inicio da string ate ao espac
            //System.out.println(tabela);//mostra tabela

            query = (query.substring(tabela.length())).trim(); // Remove o nome da tabela do inicio da query
            //System.out.println("Sem condicao sobra so " + query);

            String[] campos = null;

            where(query, tabela, campos, flag);
        }//quaisquer outros comandos nao sao aceitados
        else {
            // Mandar erro, pq nao foi encontrado nenhuma query aceitavel e terminar programa
        }
    }

    public static void endQuery(String query) throws FileNotFoundException {
        int ind_pv = query.indexOf(";");
        if (ind_pv == -1) {
            //ERRO DE SYNTAX - não exite ponto e vírgula
        }

        // int ind_esp = query.indexOf(" ");
        String query_aux;

        for (int i = 0; i < query.length(); i++) { //procura pelo ;
            if (query.charAt(i) == ';') {

                if (i == query.length() - 1) {
                    break;
                }

                query_aux = query.substring(i);
                System.out.println(query_aux);

                if (query.charAt(i + 1) == ' ') { //se a seguir ao ; estiver um espaço
                    query_aux = query_aux.substring(2); //remove esse espaço
                    System.out.println(query_aux);
                }

                query_aux = query_aux.toLowerCase();
                //query_aux = query_aux.substring(0, ind_esp); //cria uma string com o que esta entre o indice do ; e espaço

                if (query_aux.startsWith("select ")
                        || query_aux.startsWith("insert ")
                        || query_aux.startsWith("update ")
                        || query_aux.startsWith("delete ")) {
                    //NOVA QUERY
                    //Recursividade para ler esta nova(s) queries
                    query = query_aux;
                    sql_to_Java(query);
                } else if (!query_aux.startsWith("select ")
                        || !query_aux.startsWith("insert ")
                        || !query_aux.startsWith("update ")
                        || !query_aux.startsWith("delete ")) {
                    //ERRO DE SYNTAX
                }
            }
        }
    }

    public static void where(String query, String tabela, String[] campos, int flag) throws FileNotFoundException {
        if (query.equals(";")) { // Se apenas restar ;
            // Parsing Concluido
            query = "";
            // Mandar para o engine

        } else { // Analisar a cláusula WHERE
            if (!query.startsWith("where ")) {
                // Mandar erro, pq WHERE não foi encontrado ou falta ponto e virgula, e terminar programa
            }
            query = query.replaceFirst("where ", ""); // Remove o WHERE do inicio da query
            int ind_pv = query.indexOf(";");
            //System.out.println(query);
            if (ind_pv == -1) {
                // Mandar erro, pq ponto e virgula não foi encontrado, e terminar programa
            }
            String condicao = (query.substring(0, ind_pv)).trim(); // O que resta é a condição
            //System.out.println(condicao);

            String[] cond_campo_valor = condicao.split("=");

            query = "";
            // Mandar para o engine
            if (flag == 1) {
                //chama select
                System.out.println("Pesquisar na tabela \"" + tabela + "\" pelos seguintes campos:");
                for (String campo : campos) {
                    System.out.println("\t - " + campo);
                    try (PrintWriter pw = new PrintWriter(new File("pessoas.csv"))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(tabela + ";\n\n");
                        sb.append(cond_campo_valor[0] + ";\n");
                        sb.append(cond_campo_valor[1] + ";\n");
                        pw.write(sb.toString());
                    }
                }

                System.out.println("E devolver as linhas que correspondam à seguinte condição:");
                for (int i = 0; i < cond_campo_valor.length; i++) {
                    if (i % 2 == 0) {
                        System.out.println("\t - Campo = " + cond_campo_valor[0]);
                    } else {
                        System.out.println("\t - Valor = " + cond_campo_valor[1]);
                    }
                }
                //select()
            } else if (flag == 2) {
                //chama delete
                System.out.println("Eliminar na tabela " + tabela + " os campos onde:");
                for (int i = 0; i < cond_campo_valor.length; i++) {
                    if (i % 2 == 0) {
                        System.out.println("\t - Campo= " + cond_campo_valor[0]);
                    } else {
                        System.out.println("\t - Valor= " + cond_campo_valor[1]);
                    }
                    try (PrintWriter pw = new PrintWriter(new File("pessoas.csv"))) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(tabela + ";\n\n");
                        sb.append(cond_campo_valor[0] + ";\n");
                        sb.append(cond_campo_valor[1] + ";\n");
                        pw.write(sb.toString());
                    }
                }
            }
            flag = 0;
        }
    }
}
