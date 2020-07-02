import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class ServiceSystem {
    double service_positions[]; /* Serwisy obsługi, jeśli service_postion[i]==0.0 wtedy to stanowisko obsługi jest wolne*/
    double service_times[]; /* Czas obsługi dla każdego serwisu obsługi */
    double t_curr; /* Obecny czas systemu */
    int n_request; /* Liczba wygenerowanych zgłoszeń */
    int n_served_request; /* Liczba obsłużonych zgłoszeń */
    int n_rejected_request; /* Liczba odrzuconych zgłoszeń */

    /* Konstruktor */
    /* N -- liczba serwisów obsługi
       t_service_min -- minimalny czas obsługi zgłoszenia przez stanowisko
       t_service_max -- maksymalny czas obsługi zgłoszenia przez stanowisko */
    public ServiceSystem(int N, double t_service_min, double t_service_max){
        /* Ustawienie zmiennych */
        this.service_positions = new double[N];
        this.service_times = new double[N];
        SimGenerator sg = new SimGenerator(); /* utworzenie nowego generatora bez podania ziarna */
        for(int i=0; i<this.service_times.length; i++){
            this.service_times[i] = sg.uniform(t_service_min,t_service_max); /* wygenerowanie nowej liczby o rozkładzie jednostajnym (t_service_min, t_service_max) - czas obsługi przez dane stanowisko */
        }
        resetSystem();
    }

    /* Symulacja systemu do czasu t_fin lub do wygenerowania n_fin zgłoszeń */
    public void simulate(double a, double t_fin, int n_fin) throws Exception {
        /* Weryfikacja czy parametry systemu są ustawione poprawnie - czy system został zresetowany*/
        if(t_curr !=0 || n_request !=0 || isAnyServicePositionBusy() || n_served_request != 0 || n_rejected_request !=0) {
            throw new Exception("ServiceSystem has NOT been reset after simulation."); /*System obsługi nie został zresetowany po symulacji. Wyrzucam wyjątek. Alternatywą do wyrzucania wyjątki jest zresetowanie systemu poprzez wywołanie metody resetSystem() */
            //resetSystem();
        }
        double t_curr_request = t_curr; //ustawienie czasu pojawienia się pierwszego zgłoszenia
        n_request++; //zwięksenie liczby zgłoszeń w systemie
        while(t_curr < t_fin &&  n_request < n_fin){
            /* Zrealizowanie obsługi zgłoszenia i zwolnienie miejsca obsługi */
            if(isAnyServicePositionBusy()){
                double t_next_served_request = getNextServedRequestTime();
                if(t_curr ==t_next_served_request) {
                    int index = getIndexOfNextServedRequest();
                    n_served_request++;
                    service_positions[index] = 0; /* Zwolnienie miejsca obsługi */
                    System.out.println(round(t_curr,2)+" -- Obsłużono zgłoszenie. Stanowisko: "+index+" zakończyło obsługę zgłoszenia.");
                }
            }

            /* Przydzielenie zgłoszenia do wolnego miejsca obsługi lub odrzucenie zgłoszenia */
            if(t_curr == t_curr_request) {
                if (isAnyServicePositionFree()) {
                    int index = getIndexOfAvailableServicePosition();
                    service_positions[index] = t_curr_request + service_times[index]; /* Ustawienie czasu, kiedy miejsce obsługi zakończy realizację zgłoszenia */
                    System.out.println(round(t_curr, 2) + " -- Rozpoczęto obsługę zgłoszenia. Stanowisko: " + index + " rozpoczęło obsługę zgłoszenia " + n_request + ". Zgłoszenie zostanie obsłużone o: " + round(service_positions[index], 2) + ".");
                } else {
                    n_rejected_request++;
                    System.out.println(round(t_curr, 2) + " -- Odrzucono zgłoszenie " + n_request + ". Brak wolnych miejsc obsługi.");
                }
                /* Wygenerowanie nowego zgłoszenia */
                /* Nowe zgłoszenie generowane jest po pojawieniu się zgłoszenia w systemie (przydzielniu go do miejsca obsługi lub odrzuceniu go) */
                t_curr_request = generateNextRequest(t_curr_request,a);
                n_request++;
                System.out.println(round(t_curr,2)+" -- Wygenerowano nowe zgłoszenie. Zgłoszenie "+n_request+" pojawi się o: "+round(t_curr_request,2)+".");
            }

            /* Ustawienie czasu systemu skokowo - do pojawienia się nowego zgłoszenia lub do obsługi zgłoszenia */
            if(isAnyServicePositionBusy()){
                double t_next_served_request = getNextServedRequestTime();
                t_curr = (t_next_served_request < t_curr_request) ? t_next_served_request : t_curr_request;
            } else {
                t_curr = t_curr_request;
            }
        }
        if(t_curr >= t_fin) {
            System.out.println("Zakończono symulację, ponieważ czas systemu przekroczył zadany czas trwania symulacji.");
            System.out.println("t_curr="+round(t_curr, 2)+" >= t_fin="+t_fin);
        } else if(n_request == n_fin) {
            System.out.println("Zakończono symulację, ponieważ wygenerowano n_fin zgłoszeń.");
            System.out.println("n_request="+n_request+" == n_fin="+n_fin);
        }
    }

    /* Zwraca czy jest jakieś wolne miejsce obsługi */
    private boolean isAnyServicePositionFree(){
        for(double service_pos:this.service_positions){
            if(service_pos == 0){
                return true;
            }
        }
        return false;
    }

    /* Zwraca czy jest jakieś zajęte miejsce obsługi */
    private boolean isAnyServicePositionBusy(){
        for(double service_pos:this.service_positions){
            if(service_pos != 0){
                return true;
            }
        }
        return false;
    }

    /* Zwraca indeks dostępnego miejsca obsługi */
    private int getIndexOfAvailableServicePosition() throws Exception {
        for(int i=0; i<this.service_positions.length; i++){
            if(this.service_positions[i] == 0){
                return i;
            }
        }
        throw new Exception("There is NOT any available service position.");
    }

    /* Zwraca czas najbliższej obsługi zgłoszenia */
    private double getNextServedRequestTime() throws Exception {
        double t_min = Double.POSITIVE_INFINITY;
        for(double service_pos:this.service_positions) {
            if(service_pos != 0 && service_pos < t_min) {
                t_min = service_pos;
            }
        }
        if(t_min == Double.POSITIVE_INFINITY){
            throw new Exception("There is NOT any busy service position.");
        }
        return t_min;
    }

    /* Zwraca indeks stanowiska, na którym najbliżej zostanie obsłużone zgłoszenie */
    private int getIndexOfNextServedRequest() throws Exception {
        double t_min = Double.POSITIVE_INFINITY;
        int index = -1;
        for(int i=0; i<this.service_positions.length; i++) {
            if(service_positions[i] != 0 && service_positions[i] < t_min) {
                t_min = service_positions[i];
                index = i;
            }
        }
        if(t_min == Double.POSITIVE_INFINITY){
            throw new Exception("There is NOT any busy service position.");
        } else if (index == -1) {
            throw new Exception("There is NOT any busy service position.");
        }
        return index;
    }

    /* Wygenerowanie czasu pojawienia się kolejnego zgłoszenia */
    private double generateNextRequest(double curr_request, double a) {
        SimGenerator sg = new SimGenerator(); /* utworzenie nowego generatora bez podania ziarna */
        double interval = sg.exponential(a); /* wygenerowanie nowej liczby o rozkładzie wykładniczym (a) - odstęp czasiu między pojawieniem się kolejnego zgłoszenia */
        double next_request = curr_request+interval; /* czas pojawienia się kolejnego zgłoszenia */
        return next_request;
    }

    /* Resetuje zmienne systemu */
    public void resetSystem() {
        Arrays.fill(this.service_positions, 0);
        this.t_curr = 0.0;
        this.n_request = 0;
        this.n_served_request = 0;
        this.n_rejected_request = 0;
    }

    /* Zaokrąglanie doubli do określonej liczby miejsc po przecinku */
    /* Wykorzystuję przy wypisywaniu komunikatów przez system
       Czas zaokrąglam do dwóch miejsc po przecinku, aby komunikaty były czytelne */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}