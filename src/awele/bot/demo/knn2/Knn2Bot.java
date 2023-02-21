package awele.bot.demo.knn2;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Second bot qui utilise l'algorithm k-NN pour faire des prédictions
 */
public class Knn2Bot extends DemoBot
{
    private static final int k = 10;
    private Knn2Data won;
    private Knn2Data lost;
    
    /**
     * @throws InvalidBotException
     */
    public Knn2Bot () throws InvalidBotException
    {
        this.setBotName ("k-NN2");
        this.addAuthor ("Alexandre Blansché");
    }

    /**
     * Rien à faire
     */
    @Override
    public void initialize ()
    {
    }

    /**
     * Pour une situation donnée, on regarde les situations les plus proches dans les deux bases de données
     * La priorité pour chaque coup augmente selon le nombre de voisins pour ce coup sur la base du gagnant
     * La priorité pour chaque coup diminue selon le nombre de voisins pour ce coup sur la base du perdant
     */
    @Override
    public double [] getDecision (Board board)
    {
        int [] x = new int [12];
        int [] holes = board.getPlayerHoles ();
        for (int i = 0; i < 6; i++)
            x [i] = holes [i];
        holes = board.getOpponentHoles ();
        for (int i = 0; i < 6; i++)
            x [i + 6] = holes [i];
        double [] neighborsWon = this.won.countNeighbors (x, Knn2Bot.k);
        double [] neighborsLost = this.lost.countNeighbors (x, Knn2Bot.k); 
        double [] decision = new double [6];
        for (int i = 0; i < decision.length; i++)
            decision [i] = neighborsWon [i] - neighborsLost [i];
        return decision;
    }

    /**
     * Création des données : un ensemble pour les coups joués par le gagnant et un autre pour ceux du perdant
     */
    @Override
    public void learn ()
    {
        this.won = new Knn2Data (true);
        this.lost = new Knn2Data (false);
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
