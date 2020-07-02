import java.util.ArrayList;
import javafx.util.Pair;

public class MonteCarlo {
    IFunction f; /* Funkcja z której będzie liczona całka */
    double x_p; /* Początek przedziału całkowania */
    double x_k; /* Koniec przedziału całkowania */
    double m; /* Oczekiwana wartość całki z funkcji f w przedziale <x_p, x_k> (wartość rzeczywista) */

    /* Konstruktor */
    public MonteCarlo(IFunction f, double x_p, double x_k, double m){
        /* Ustawienie zmiennych */
        this.f = f;
        /* Sprawdzenie czy przedział całkowania został określony poprawnie */
        if(x_p > x_k) throw new IllegalArgumentException("x_k cannot be lower than x_p");
        this.x_p = x_p;
        this.x_k = x_k;
        this.m = m;
    }

    /* Szacowanie wartości całki z funkcji f za pomocą metody prostokątów dla podanej liczby punktów n
       Punkty generowane są za pomocą metody generateBasicPoints */
    public double calculateBasicIntegration(int n){
        double integral = 0.0;
        ArrayList<Double> points = generateBasicPoints(n);
        points.sort(Double::compare);
        for (int i=1; i<points.size(); i++) {
            double length = points.get(i)-points.get(i-1); /* długość prostokąta */
            double height = f.compute(points.get(i)); /* wysokość prostokąta */
            double area = length*height; /* pole prostokąta */
            integral += area;
        }
        return integral;
    }

    /* Wygenerowanie n unikalnych punktów z przedziału <x_p, x_k> zgodnie z rozkładem równomiernym */
    private ArrayList<Double> generateBasicPoints(int n){
        ArrayList<Double> points = new ArrayList<>();
        for(int i=0; i<n; i++){
            SimGenerator sg = new SimGenerator();	/* utworzenie nowego generatora bez podania ziarna */
            double x = sg.uniform(this.x_p, this.x_k);	/* wygenerowanie nowej liczby o rozkładzie równomiernym (x_p, x_k) */
            /* dodanie do listy points tylko unikalnych punktów */
            while(points.contains(x)) {
                x = sg.uniform(this.x_p, this.x_k);
            }
            points.add(x);
        }
        return points;
    }

    /* Szacowanie wartości całki z funkcji f za pomocą metody Monte Carlo
       Zadanie dodatkowe */
    public double calculateAdditionalIntegration(int n, double y_min, double y_max){
        /* Sprawdzenie czy przedział <y_min, y_max> został określony poprawnie */
        if(y_min > y_max) throw new IllegalArgumentException("y_max cannot be lower than y_min");
        int points_in = 0;
        ArrayList<Pair<Double,Double>> points = generateAdditionalPoints(n, y_min, y_max);
        /* Sprawdzenie ile wygenerowanych punktów znajduje się pod wykresem */
        for(Pair<Double, Double> point:points) {
            double y = this.f.compute(point.getKey());
            double y_rand = point.getValue();
            /* Jeśli wygenerowana wartość y_rand jest pod wykresem i nad osią X */
            if((y>=0 && y>=y_rand)){
                points_in++;
            }
            /* Jeśli wygenerowana wartość y_rand jest nad wykresem i pod osią X */
            else if(y<0 && y<=y_rand) {
                points_in--;
            }
        }
        double integral_norm = (double)points_in/points.size();
        double integral = (integral_norm)*(this.x_k-this.x_p)*(y_max-y_min); //denormalizacja
        return integral;
    }

    /* Wygenerowanie n unikalnych punktów z przedziału <x_p, x_k> oraz n unikalnych punktów z przedziału <y_min, y_max> zgodnie z rozkładem równomiernym */
    private ArrayList<Pair<Double,Double>> generateAdditionalPoints(int n, double y_min, double y_max) {
        ArrayList<Pair<Double,Double>> points = new ArrayList<>();
        for(int i=0; i<n; i++){
            SimGenerator sg = new SimGenerator();	/* utworzenie nowego generatora bez podania ziarna */
            double x = sg.uniform(this.x_p, this.x_k);	/* wygenerowanie nowej liczby o rozkładzie równomiernym (x_p, x_k) */
            double y = sg.uniform(y_min, y_max);	/* wygenerowanie nowej liczby o rozkładzie równomiernym (y_min, y_max) */
            Pair<Double, Double> p = new Pair<>(x,y);
            /* dodanie do listy points tylko unikalnych par */
            while(points.contains(p)) {
                y = sg.uniform(y_min, y_max);
                p = new Pair<>(x,y);
            }
            points.add(p);
        }
        return points;
    }

    /* Getter dla m -- oczekiwanej wartości całki z funkcji f */
    public double getExpectedValue() {
        return this.m;
    }
}
