public class Main {

    public static void main(String[] args) {
        /* ZADANIE 1 */
        System.out.println("*******************");
        System.out.println("ZADANIE 1:");
        /* Utworzenie obiektu klasy MonteCarlo */
        /* Przy tworzeniu obiektu monteCarlo1 podano następujące argumenty:
           IFunction f1=3/x -- dla tej funkcji będzie liczona całka
           double x_p=1 -- początek przedziału całkowania
           double x_k=e -- koniec przedziału całkowania
           double m=3 -- rzeczywista wartość całki z funkcji f1(x) w przedziale <1,e> (wartość oczekiwana) */
        MonteCarlo monteCarlo1 = new MonteCarlo(x -> (double)3/x,1, Math.E, 3);
        /* Oszacowanie wartości całki z funkcji f1 za pomocą metody prostokątów dla różnej liczby punktów */
        System.out.println("Szacowanie wartości całki z funkcji f1 za pomocą metody prostokątów dla różnej liczby punktów:");
        int[] ns_1 = {1,2,5,10,20,50,100,1000}; //testowy zbiór liczby punktów do Zadania 1
        for(int n: ns_1){
            double m_pred = monteCarlo1.calculateBasicIntegration(n);
            double m = monteCarlo1.getExpectedValue();
            double error = Math.abs((m_pred-m)/m*100);
            System.out.println("*\tdla n="+n);
            System.out.println("\tOszacowana wartość wynosi:"+m_pred+";");
            System.out.println("\tOczekiwana wartość: "+m+";");
            System.out.printf("\tBłąd: %.2f%%\n", error);
        }

        /* ZADANIE DODATKOWE */
        System.out.println("*******************");
        System.out.println("ZADANIE DODATKOWE:");

        /* Utworzenie obiektu klasy MonteCarlo */
        /* Przy tworzeniu obiektu monteCarlo2 podano następujące argumenty:
           IFunction f2=x -- dla tej funkcji będzie liczona całka
           double x_p=-1 -- początek przedziału całkowania
           double x_k=2 -- koniec przedziału całkowania
           double m=1.5 -- rzeczywista wartość całki z funkcji f2(x) w przedziale <-1,2> (wartość oczekiwana) */
        MonteCarlo monteCarlo2 = new MonteCarlo(x -> x,-1, 2, 1.5);
        int[] ns_2 = {1,2,5,10,20,50,100,1000}; //testowy zbiór liczby punktów dla Zadania 2

        /* Oszacowanie wartości całki z funkcji f2 za pomocą metody Monte Carlo */
        System.out.println("Szacowanie wartości całki z funkcji f2 za pomocą metody Monte Carlo dla różnej liczby punktów:");
        /* Prostokąt na określonym przez użytkownika przedziale nie zawsze musi przecinać oś X
           W takich przypadkach, aby nie modyfikować funkcji denormalizacji zdecydowałem się na 'wydłużenie' prostokąta */
        double y_min = Math.min(0,monteCarlo2.f.compute(monteCarlo2.x_p));
        double y_max = Math.max(0,monteCarlo2.f.compute(monteCarlo2.x_k));
        for(int n: ns_2){
            double m_pred = monteCarlo2.calculateAdditionalIntegration(n, y_min, y_max);
            double m = monteCarlo2.getExpectedValue();
            double error = Math.abs((m_pred-m)/m*100);
            System.out.println("*\tdla n="+n);
            System.out.println("\tOszacowana wartość wynosi:"+m_pred+";");
            System.out.println("\tOczekiwana wartość: "+m+";");
            System.out.printf("\tBłąd: %.2f%%\n", error);
        }
        /* *************************************** */
        /* Oszacowanie wartości całki z funkcji f1 z Zadania 1 za pomocą metody Monte Carlo */
        System.out.println("Szacowanie wartości całki z funkcji f1 z Zadania 1 za pomocą metody Monte Carlo dla różnej liczby punktów:");
        for(int n: ns_2){
            double m_pred = monteCarlo1.calculateAdditionalIntegration(n, Math.min(0,monteCarlo1.f.compute(monteCarlo1.x_k)), Math.max(0,monteCarlo1.f.compute(monteCarlo1.x_p)));
            double m = monteCarlo1.getExpectedValue();
            double error = Math.abs((m_pred-m)/m*100);
            System.out.println("*\tdla n="+n);
            System.out.println("\tOszacowana wartość wynosi:"+m_pred+";");
            System.out.println("\tOczekiwana wartość: "+m+";");
            System.out.printf("\tBłąd: %.2f%%\n", error);
        }

        /* ZADANIE 2 */
        System.out.println("*******************");
        System.out.println("ZADANIE 2:");
        /* Ustawienie parametrów */
        int N = 2; /* Liczba serwisów obsługi */
        double t_service_min = 3; /* Minimalny czas obsługi zgłoszenia przez stanowisko */
        double t_service_max = 7; /* Maksymalny czas obsługi zgłoszenia przez stanowisko */

        ServiceSystem serviceSystem = new ServiceSystem(N, t_service_min, t_service_max);
        System.out.println("Symulacja systemu obsługi:");
        try {
            double a = 1; /* Parametr funkcji wykładniczej expotential() */
            double t_fin = 10; /* Maksymalny czas trwania symulacji */
            int n_fin = 50; /* Maksymalna liczba zgłoszeń do wygenerowania podczas symulacji */
            serviceSystem.simulate(a, t_fin, n_fin);
            System.out.println("* System obsłużył: "+serviceSystem.n_served_request+" zgłoszeń.");
            System.out.println("* System odrzucił: "+serviceSystem.n_rejected_request+" zgłoszeń.");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /* *************************************** */
        /* Symulacja tego samego systemu obsługi drugi raz - wywołanie metody simulate, ale z innymi argumentami niż poprzednio */
        System.out.println("**********");
        System.out.println("Symulacja systemu obsługi drugi raz, ale z innymi argumentami niż poprzednio:");
        try {
            serviceSystem.resetSystem(); /* Trzeba zresetować parametry systemu - w przeciwnym wypadku zostanie wyrzucony wyjątek */
            double a = 1; /* Parametr funkcji wykładniczej expotential() */
            double t_fin = 100; /* Maksymalny czas trwania symulacji */
            int n_fin = 10; /* Maksymalna liczba zgłoszeń do wygenerowania podczas symulacji */
            serviceSystem.simulate(a, t_fin, n_fin);
            System.out.println("* System obsłużył: "+serviceSystem.n_served_request+" zgłoszeń.");
            System.out.println("* System odrzucił: "+serviceSystem.n_rejected_request+" zgłoszeń.");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
