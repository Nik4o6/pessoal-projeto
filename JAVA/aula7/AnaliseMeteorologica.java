public class AnaliseMeteorologica {

    // ===== ATRIBUTOS GLOBAIS =====
    static double[][] temperaturas = new double[5][2]; // [cidade][0=max, 1=min]
    static int[][] umidades = new int[5][3]; // [cidade][0=manha,1=tarde,2=noite]
    static String[] cidades = {"Cidade1","Cidade2","Cidade3","Cidade4","Cidade5"};

    public static void m                                                                                            ain(String[] args) {
        // 1️⃣ Inicialização
        inicializarDados();

        // 2️⃣ Processamento
        calcularEstatisticasAvancadas();
        identificarCidadeComMaiorAmplitudeTermica();

        // 3️⃣ Saída
        gerarRelatorioDetalhado();
    }

    // ===== MÉTODOS =====

    // 1. Inicialização
    static void inicializarDados() {
        // Exemplo: hard-coded
        temperaturas[0][0] = 32; temperaturas[0][1] = 22;
        temperaturas[1][0] = 28; temperaturas[1][1] = 18;
        temperaturas[2][0] = 35; temperaturas[2][1] = 25;
        temperaturas[3][0] = 20; temperaturas[3][1] = 10;
        temperaturas[4][0] = 30; temperaturas[4][1] = 20;

        umidades[0][0] = 70; umidades[0][1] = 65; umidades[0][2] = 60;
        umidades[1][0] = 55; umidades[1][1] = 60; umidades[1][2] = 50;
        umidades[2][0] = 80; umidades[2][1] = 85; umidades[2][2] = 75;
        umidades[3][0] = 40; umidades[3][1] = 35; umidades[3][2] = 30;
        umidades[4][0] = 60; umidades[4][1] = 65; umidades[4][2] = 55;
    }

    // 2. Média ponderada
    static double calcularMediaPonderadaTemperatura(double max, double min) {
        // validar intervalo
        if(max < -50 || max > 60 || min < -50 || min > 60) return -999;
        return max * 0.7 + min * 0.3;
    }

    // 3. Classificação do clima
    static String classificarClima(double tempMedia, int umidadeMedia) {
        if(tempMedia > 30 && umidadeMedia > 75) return "MUITO QUENTE E ÚMIDO";
        else if(tempMedia >= 20 && tempMedia <= 25 && umidadeMedia >= 50 && umidadeMedia <= 70) return "CONFORTAVEL";
        else if(tempMedia < 15 && umidadeMedia < 50) return "FRIO E SECO";
        else return "NORMAL";
    }

    // 4. Cidade com maior amplitude térmica
    static int identificarCidadeComMaiorAmplitudeTermica() {
        int indice = 0;
        double maiorAmplitude = 0;
        for(int i=0;i<temperaturas.length;i++){
            double amplitude = temperaturas[i][0] - temperaturas[i][1];
            if(amplitude > maiorAmplitude){
                maiorAmplitude = amplitude;
                indice = i;
            }
        }
        return indice;
    }

    // 5. Índice de calor
    static double calcularIndiceCalor(double temp, int umidade) {
        double indice = temp + 0.5 * (umidade/100.0) * (temp - 20);
        return Math.round(indice*10.0)/10.0;
    }

    // 6. Gerar alertas
    static int gerarAlertas(int cidadeIndex) {
        double tempMax = temperaturas[cidadeIndex][0];
        double tempMin = temperaturas[cidadeIndex][1];
        int umidMax = Math.max(Math.max(umidades[cidadeIndex][0], umidades[cidadeIndex][1]), umidades[cidadeIndex][2]);
        double variacao = tempMax - tempMin;

        if(tempMax > 35 || umidMax > 90) return 2; // VERMELHO
        else if((tempMax >= 30 && tempMax <= 35 && umidMax > 80) || variacao > 15) return 1; // AMARELO
        else return 0; // VERDE
    }

    // 7. Estatísticas avançadas
    static double[] calcularEstatisticasAvancadas() {
        double soma = 0, maior = -999, menor = 999, somaDesvio = 0;
        int total = temperaturas.length * 2;
        for(int i=0;i<temperaturas.length;i++){
            for(int j=0;j<2;j++){
                double t = temperaturas[i][j];
                soma += t;
                if(t>maior) maior = t;
                if(t<menor) menor = t;
            }
        }
        double media = soma/total;
        for(int i=0;i<temperaturas.length;i++){
            for(int j=0;j<2;j++){
                somaDesvio += Math.pow(temperaturas[i][j]-media,2);
            }
        }
        double desvio = Math.sqrt(somaDesvio/total);
        return new double[]{media, maior, menor, desvio};
    }

    // 8. Comparar cidades
    static String compararCidades(int cidade1, int cidade2) {
        double media1 = calcularMediaPonderadaTemperatura(temperaturas[cidade1][0], temperaturas[cidade1][1]);
        double media2 = calcularMediaPonderadaTemperatura(temperaturas[cidade2][0], temperaturas[cidade2][1]);
        int umid1 = (umidades[cidade1][0]+umidades[cidade1][1]+umidades[cidade1][2])/3;
        int umid2 = (umidades[cidade2][0]+umidades[cidade2][1]+umidades[cidade2][2])/3;

        String maisQuente = media1 > media2 ? cidades[cidade1] : cidades[cidade2];
        String maisUmida = umid1 > umid2 ? cidades[cidade1] : cidades[cidade2];
        String maisConfortavel = classificarClima(media1, umid1).equals("CONFORTAVEL") ? cidades[cidade1] : cidades[cidade2];

        return "Mais quente: "+maisQuente+" | Mais úmida: "+maisUmida+" | Mais confortável: "+maisConfortavel;
    }

    // 9. Relatório detalhado
    static void gerarRelatorioDetalhado() {
        System.out.printf("%-10s %-10s %-10s %-15s %-10s\n","Cidade","Temp Max","Temp Min","Media Temp","Alerta");
        for(int i=0;i<cidades.length;i++){
            double media = calcularMediaPonderadaTemperatura(temperaturas[i][0], temperaturas[i][1]);
            int alerta = gerarAlertas(i);
            String cor = alerta==2 ? "\u001B[31m" : (alerta==1 ? "\u001B[33m" : "\u001B[32m"); // ANSI
            System.out.printf("%-10s %-10.1f %-10.1f %-15.1f %s%-10s\u001B[0m\n",cidades[i],temperaturas[i][0],temperaturas[i][1],media,cor,(alerta==2?"VERMELHO":alerta==1?"AMARELO":"VERDE"));
        }
    }
}