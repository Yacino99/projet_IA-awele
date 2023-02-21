package awele.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author Alexandre Blansché
 * Classe représentant l'état du plateau de jeu
 */
public class Board
{
    /**
     * Nombre de trou de chaque côté du plateau de jeu
     */
    public static final int NB_HOLES = 6;
    private static final int NB_SEEDS = 4;
    int [][] holes;
    private int [] score;
    int currentPlayer;
    private List<List<Integer>> log;
    private Random random;
    
    /**
     * Constructeur...
     */
    public Board ()
    {
        this.random = new Random (1 + System.currentTimeMillis ());
        this.score = new int [2];
        this.holes = new int [2][Board.NB_HOLES];
        for (int i = 0; i < Board.NB_HOLES; i++)
        {
            this.holes [0][i] = Board.NB_SEEDS;
            this.holes [1][i] = Board.NB_SEEDS;
        }
        this.log = new ArrayList<List<Integer>> ();
        this.log.add (new ArrayList<Integer> ());
        this.log.add (new ArrayList<Integer> ());
    }
    
    /**
     * @param player L'indice d'un joueur
     * @return Liste des coups joués par ce joueur (dans l'ordre chronologique)
     */
    public int [] getLog (int player)
    {
        return this.log.get (player).stream ().mapToInt (Integer::intValue).toArray ();
    }
    
    /**
     * @return Le nombre de graines encore en jeu
     */
    public int getNbSeeds ()
    {
        int sum = 0;
        for (int i = 0; i < Board.NB_HOLES; i++)
            sum += this.holes [0][i] + this.holes [1][i];
        return sum;
    }
    
    /**
     * @param player L'index du joueur courant
     * @return Le nombre de graines encore en jeu du côté du joueur courant
     */
    public int getPlayerSeeds ()
    {
        int sum = 0;
        for (int i = 0; i < Board.NB_HOLES; i++)
            sum += this.holes [this.currentPlayer][i];
        return sum;
    }
    
    /**
     * @param player L'index du joueur adverse
     * @return Le nombre de graines encore en jeu du côté du joueur adverse
     */
    public int getOpponentSeeds ()
    {
        int sum = 0;
        for (int i = 0; i < Board.NB_HOLES; i++)
            sum += this.holes [Board.otherPlayer (this.currentPlayer)][i];
        return sum;
    }
    
    /**
     * @return Le nombre de graine dans chaque trou du joueur courant
     */
    public int [] getPlayerHoles ()
    {
        int [] holes = new int [this.holes [this.currentPlayer].length];
        for (int i = 0; i < holes.length; i++)
            holes [i] = this.holes [this.currentPlayer][i];
        return holes;
    }

    /**
     * @return Le nombre de graine dans chaque trou du joueur adverse
     */
    public int [] getOpponentHoles ()
    {
        int otherPlayer = Board.otherPlayer (this.currentPlayer);
        int [] holes = new int [this.holes [otherPlayer].length];
        for (int i = 0; i < holes.length; i++)
            holes [i] = this.holes [otherPlayer][i];
        return holes;
    }
    
    void setCurrentPlayer (int currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }
    
    void changeCurrentPlayer ()
    {
        this.setCurrentPlayer (Board.otherPlayer (this.currentPlayer));
    }
    
    /**
     * @param player L'indice d'un joueur
     * @return Retourne l'indice de l'autre joueur
     */
    public static int otherPlayer (int player)
    {
        return 1 - player;
    }
    
    int getNbSeeds (int player)
    {
        int sum = 0;
        for (int i = 0; i < Board.NB_HOLES; i++)
            sum += this.holes [player][i];
        return sum;
    }
    
    /**
     * @param player L'indice d'un joueur
     * @return Indique si le joueur n'a plus de graine
     */
    public boolean isEmpty (int player)
    {
        return this.getNbSeeds (player) == 0;
    }
    
    /**
     * @param player L'indice d'un joueur
     * @return Indique les coups valides et non valides
     */
    public boolean [] validMoves (int player)
    {
        boolean [] valid = new boolean [Board.NB_HOLES];
        boolean notEmpty = !this.isEmpty (Board.otherPlayer (player));
        for (int i = 0; i < Board.NB_HOLES; i++)
            valid [i] = (this.holes [player][i] > 0) && (notEmpty || (i + this.holes [player][i] >= 6));
        return valid;
    }
    
    private int selectMove (int player, double [] decision) throws InvalidBotException
    {
        boolean [] valid = this.validMoves (player);
        int bestMove = -1;
        double bestDecision = -Double.MAX_VALUE;
        int nbBest = 0;
        for (int i = 0; i < Board.NB_HOLES; i++)
            if (Double.isNaN (decision [i]))
                throw new InvalidBotException ("NaN dans le vecteur de prise de décision");
            else if (valid [i])
                if (decision [i] > bestDecision)
                {
                    bestDecision = decision [i];
                    nbBest = 1;
                }
                else if (decision [i] == bestDecision)
                    nbBest++;
        if (nbBest > 0)
        {
            int select = this.random.nextInt (nbBest);
            loop:
                for (int i = 0; i < Board.NB_HOLES; i++)
                    if (valid [i] && (decision [i] == bestDecision))
                        if (select == 0)
                        {
                            bestMove = i;
                            break loop;
                        }
                        else
                            select--;
        }
        return bestMove;
    }
    
    private boolean takeAll (int player, int currentHole)
    {
        boolean takeAll = true;
        int opponent = Board.otherPlayer (player);
        for (int i = 0; i <= currentHole; i++)
            if ((this.holes [opponent][i] == 1) || (this.holes [opponent][i] > 3))
                takeAll = false;
        for (int i = currentHole + 1; i < Board.NB_HOLES; i++)
            if (this.holes [opponent][i] != 0)
                takeAll = false;
        return takeAll;
    }
    
    void addLog (int currentPlayer, int move)
    {
        this.log.get (currentPlayer).add (move);
    }
    
    int playMove (int player, double [] decision) throws InvalidBotException
    {
        int score = 0;
        int bestMove = this.selectMove (player, decision);
        if (bestMove >= 0)
        {
            this.addLog (player, bestMove);
            int nbSeeds = this.holes [player][bestMove];
            this.holes [player][bestMove] = 0;
            int currentSide = player;
            int currentHole = bestMove;
            while (nbSeeds > 0)
            {
                currentHole++;
                if (currentHole >= Board.NB_HOLES)
                {
                    currentSide = Board.otherPlayer (currentSide);
                    currentHole = 0;
                }
                if ((currentSide != player) || (currentHole != bestMove))
                {
                    this.holes [currentSide][currentHole]++;
                    nbSeeds--;
                }
            }
            if ((currentSide == Board.otherPlayer (player))
                    && ((this.holes [currentSide][currentHole] == 2) || (this.holes [currentSide][currentHole] == 3))
                    && !this.takeAll (player, currentHole))
            {
                while ((currentHole >= 0)
                        && ((this.holes [currentSide][currentHole] == 2) || (this.holes [currentSide][currentHole] == 3)))
                {
                    score += this.holes [currentSide][currentHole];
                    this.holes [currentSide][currentHole] = 0;
                    currentHole--;
                }
            }
        }
        else
            score = -1;
        if (score < 0)
        {
            this.score [this.currentPlayer] += this.getNbSeeds (this.currentPlayer);
            Arrays.fill (this.holes [this.currentPlayer], 0);
        }
        else
        {
            this.score [this.currentPlayer] += score;
            this.changeCurrentPlayer ();
        }
        return score;
    }
    
    /**
     * @return L'indice du joueur courant
     */
    public int getCurrentPlayer ()
    {
        return this.currentPlayer;
    }
    
    /**
     * Joue un coup sur une copie du plateau et retourne le score
     * 
     * @param player L'indice du joueur qui joue le coup
     * @param decision Un tableau de six réels indiquant l'efficacité supposée de chacun des six coups possibles
     * @return Le score obtenu en jouant le coup
     * @throws InvalidBotException
     */
    public int playMoveSimulationScore (int player, double [] decision) throws InvalidBotException
    {
        Board clone = (Board) this.clone ();
        return clone.playMove (player, decision);
    }
    
    /**
     * Joue un coup sur une copie du plateau et retourne le nouvel état du plateau
     * 
     * @param player L'indice du joueur qui joue le coup
     * @param decision Un tableau de six réels indiquant l'efficacité supposée de chacun des six coups possibles
     * @return Le nouvel état du plateau en jouant le coup
     * @throws InvalidBotException 
     */
    public Board playMoveSimulationBoard (int player, double [] decision) throws InvalidBotException
    {
        Board clone = (Board) this.clone ();
        clone.playMove (player, decision);
        return clone;
    }

    @Override
    public String toString ()
    {
        String string = "|";
        for (int i = Board.NB_HOLES - 1; i >= 0; i--)
        {
            if (this.holes [1][i] < 10)
                string += " ";
            string += this.holes [1][i] + "|";
        }
        string += "\n|";
        for (int i = 0; i < Board.NB_HOLES; i++)
        {
            if (this.holes [0][i] < 10)
                string += " ";
            string += this.holes [0][i] + "|";
        }
        return string;
    }

    @Override
    public Object clone ()
    {
        Board clone = new Board ();
        clone.currentPlayer = this.currentPlayer;
        clone.score [0] = this.score [0];
        clone.score [1] = this.score [1];
        for (int i = 0; i < Board.NB_HOLES; i++)
        {
            clone.holes [0][i] = this.holes [0][i];
            clone.holes [1][i] = this.holes [1][i];
        }
        clone.log = new ArrayList<List<Integer>> ();
        clone.log.add (new ArrayList<Integer> ());
        for (Integer i: this.log.get (0))
            clone.log.get (0).add (i);
        clone.log.add (new ArrayList<Integer> ());
        for (Integer i: this.log.get (1))
            clone.log.get (1).add (i);
        return clone;
    }

    /**
     * @param player L'indice d'un joueur
     * @return Le score du joueur
     */
    public int getScore (int player)
    {
        return this.score [player];
    }
}
