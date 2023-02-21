package awele.bot.demo.knn2;

import java.util.Arrays;

import awele.core.Board;
import awele.data.AweleData;
import awele.data.AweleObservation;

/**
 * @author Alexandre Blansché
 * Données pour la seconde version de l'algorithme k-NN
 */
public class Knn2Data
{
    int [][] data;
    
    /**
     * On accède au données et on récupère toutes les observations le joueur
     * @param won indique si l'on considère les coups joués par le gagnant (true) ou par le perdant (false)
     */
    public Knn2Data (boolean won)
    {
        AweleData data = AweleData.getInstance ();
        int nb = 0;
        for (AweleObservation observation: data)
            if (observation.isWon () != !won)
                nb++;
        this.data = new int [nb][13];
        int i = 0;
        for (AweleObservation observation: data)
            if (observation.isWon () != !won)
            {
                for (int j = 0; j < 6; j++)
                    this.data [i][j] = observation.getPlayerHoles () [j];
                for (int j = 0; j < 6; j++)
                    this.data [i][j + 6] = observation.getOppenentHoles () [j];
                this.data [i][12] = observation.getMove ();
                i++;
            }
    }
    
    private static double squareDistance (int [] x1, int [] x2)
    {
        double dist = 0;
        for (int i = 0; i < 12; i++)
        {
            int diff = x1 [i] - x2 [i];
            dist += diff * diff;
        }
        return dist;
    }
    
    private double [] squareDistances (int [] x)
    {
        double [] distances = new double [this.data.length];
        for (int i = 0; i < distances.length; i++)
            distances [i] = Knn2Data.squareDistance (x, this.data [i]);
        return distances;
    }
    
    private static double getThreshold (double [] distances, int k)
    {
        double [] copy = new double [distances.length];
        System.arraycopy (distances, 0, copy, 0, distances.length);
        Arrays.sort (copy);
        double threshold = copy [k];
        return threshold;
    }

    /**
     * @param x Une situation de jeu
     * @param k Un nombre de voisin
     * @return Un tableau contenant le nombre de voisins de la situation donnée pour chaque coup jouable
     */
    public double [] countNeighbors (int [] x, int k)
    {
        double [] neighbors = new double [Board.NB_HOLES];
        double [] distances = this.squareDistances (x);
        double threshold = Knn2Data.getThreshold (distances, k);
        for (int i = 0; i < distances.length; i++)
            if (distances [i] < threshold)
                neighbors [this.data [i][12] - 1] += 1;
        return neighbors;
    }
}
