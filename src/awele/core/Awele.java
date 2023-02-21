package awele.core;

import awele.bot.Bot;
import awele.output.OutputWriter;

/**
 * @author Alexandre Blansché
 * Classe représentant une partie d'Awele entre deux joueurs
 */
public class Awele extends OutputWriter
{
    private static final int MAX_STAGNANT = 1000;
    private Bot [] players;
    private int [] scores;
    private double nbMoves;
    private long runningTime;

    /**
     * @param player1 Le premier joueur
     * @param player2 Le second joueur
     */
    public Awele (Bot player1, Bot player2)
    {
        this.players = new Bot [2];
        this.players [0] = player1;
        this.players [1] = player2;
        this.scores = new int [2];
        this.nbMoves = 0;
        this.runningTime = 0;
    }
    
    private int [] game (int firstPlayer) throws InvalidBotException
    {
        boolean end = false;
        Board board = new Board ();
        board.setCurrentPlayer (firstPlayer);
        this.printDebug ();
        this.printDebug (board);
        this.printDebug ("Score : " + board.getScore (0) + " - " + board.getScore (1));
        int nbStagnant = 0;
        while (!end)
        {
            int currentPlayer = board.getCurrentPlayer ();
            this.nbMoves += 1;
            double [] decision = this.players [currentPlayer].getDecision (board);
            int moveScore = board.playMove (currentPlayer, decision);
            if (moveScore > 0)
                nbStagnant = 0;
            else
                nbStagnant++;
            if ((moveScore < 0) ||
                    (board.getScore (Board.otherPlayer (board.getCurrentPlayer ())) >= 25) ||
                    (board.getNbSeeds () <= 6) ||
                    (nbStagnant >= Awele.MAX_STAGNANT))
                end = true;
            this.printDebug ();
            this.printDebug (board);
            this.printDebug ("Score : " + board.getScore (0) + " - " + board.getScore (1));
        }
        this.printDebug ();
        int [] score = new int [2];
        score [0] = board.getScore (0);
        score [1] = board.getScore (1);
        return score;
    }
    
    /**
     * @return Le nombre de coups joués
     */
    public double getNbMoves ()
    {
        return this.nbMoves;
    }
    
    /**
     * @return La durée de l'affrontement
     */
    public long getRunningTime ()
    {
        return this.runningTime;
    }
    
    /**
     * Fait jouer deux parties d'Awele entre les deux bots
     * @throws InvalidBotException 
     */
    public void play () throws InvalidBotException
    {
        this.print ("Partie 1");
        long start = System.currentTimeMillis ();
        this.players [0].initialize ();
        this.players [1].initialize ();
        int [] game1Score = this.game (0);
        this.players [0].finish ();
        this.players [1].finish ();
        this.runningTime += System.currentTimeMillis () - start;
        this.print ("Score: " + game1Score [0] + " - " + game1Score [1]);
        this.print ("Partie 2");
        start = System.currentTimeMillis ();
        this.players [0].initialize ();
        this.players [1].initialize ();
        int [] game2Score = this.game (1);
        this.players [0].finish ();
        this.players [1].finish ();
        this.runningTime += System.currentTimeMillis () - start;
        this.print ("Score: " + game2Score [0] + " - " + game2Score [1]);
        this.runningTime /= 2;
        this.nbMoves /= 2.;
        this.scores [0] = game1Score [0] + game2Score [0];
        this.scores [1] = game1Score [1] + game2Score [1];
        int winner = this.getWinner ();
        if (winner < 0)
            this.printDebug ("Égalité");
        else
            this.printDebug ("Gagnant : " + this.players [winner]);
    }
    
    /**
     * @return 0 si le premier bot a gagné, 1 si le second a gagné, -1 s'il y a égalité
     */
    public int getWinner ()
    {
        int winner = -1;
        if (this.scores [0] > this.scores [1])
            winner = 0;
        else if (this.scores [1] > this.scores [0])
            winner = 1;
        return winner;
    }
}
