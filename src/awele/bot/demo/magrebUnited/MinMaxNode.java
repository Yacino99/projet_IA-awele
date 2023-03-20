package awele.bot.demo.magrebUnited;

import awele.core.Board;
import awele.core.InvalidBotException;

public abstract class  MinMaxNode {

    private static int player;

    private static int maxDepth;

    private double evaluation;

    private double [] decision;


    //Algo 1
    public MinMaxNode (Board board, int depth, double alpha, double beta)
    {

        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double [Board.NB_HOLES];
        /* Initialisation de l'évaluation courante */
        this.evaluation = this.worst ();

        //Map.Entry mapentry : t.entrySet()
        /* On parcourt toutes les coups possibles */
        for (int i = 0; i < Board.NB_HOLES; i++) {

            /* Si le coup est jouable */
            if (board.getPlayerHoles () [i] != 0)
            {
                /* Sélection du coup à jouer */
                double [] decision = new double [Board.NB_HOLES];
                decision [i] = 1;
                /* On copie la grille de jeu et on joue le coup sur la copie */
                Board copy = (Board) board.clone ();
                try
                {
                    int score = copy.playMoveSimulationScore (copy.getCurrentPlayer (), decision);
                    copy = copy.playMoveSimulationBoard (copy.getCurrentPlayer (), decision);
                /* Si la nouvelle situation de jeu est un coup qui met fin à la partie,
                   on évalue la situation actuelle */
                    if ((score < 0) ||
                            (copy.getScore (Board.otherPlayer (copy.getCurrentPlayer ())) >= 25) ||
                            (copy.getNbSeeds () <= 6))
                        this.decision [i] = this.diffScore (copy);
                        /* Sinon, on explore les coups suivants */
                    else
                    {
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNode.maxDepth)
                        {
                            /* On construit le noeud suivant */
                            MinMaxNode child = this.getNextNode (copy, depth + 1, alpha, beta);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [i] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on évalue la situation actuelle */
                        else
                            this.decision [i] = this.evaluate(copy, depth);
                    }
                    /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */
                    this.evaluation = this.minmax (this.decision [i], this.evaluation);
                    /* Coupe alpha-beta */
                    if (depth > 0)
                    {
                        if (this.alphabeta (this.evaluation, alpha, beta))
                            break ;
                        alpha = this.alpha (this.evaluation, alpha); // A modifier en fonction

                        beta = this.beta (this.evaluation, beta);
                    }
                }
                catch (InvalidBotException e)
                {
                    this.decision [i] = 0;
                }
            }
        }
    }




    // Algo 1
    private double evaluate(Board boardCopy, int depth) {
        return diffScore(boardCopy) + evalSituation(boardCopy, depth);
        // return evalSituation(boardCopy, depth);

    }



    //Eval 3
    public double evalSituation(Board board, int depth) {
        int currentPlayer = board.getCurrentPlayer();
        int opponentPlayer = Board.otherPlayer(currentPlayer);
        int currentPlayerCaptured = board.getScore(currentPlayer);
        int opponentPlayerCaptured = board.getScore(opponentPlayer);

        double score = 0.0;

        // Evaluation based on number of seeds in the holes
        int[] currentPlayerHoles = board.getPlayerHoles();
        int[] opponentPlayerHoles = board.getOpponentHoles();
        for (int i = 0; i < Board.NB_HOLES; i++) {
            int seeds = currentPlayerHoles[i] - opponentPlayerHoles[Board.NB_HOLES - 1 - i];
            if (seeds >= 0) {
                score += seeds * 0.6;
            } else {
                score += seeds * 2.4;
            }
        }
        score *= 0.7;

        // Evaluation based on number of captured seeds
        score += (currentPlayerCaptured - opponentPlayerCaptured) * 2.0;
        score *= 0.2;

        // Evaluation based on difference in number of seeds and captured seeds
        double diffScore = score - currentPlayerCaptured + opponentPlayerCaptured;
        score += diffScore * 0.3;

        // Evaluation based on number of turns remaining
        double turnsRemainingScore = 1.0 / (depth + 1);
        score += turnsRemainingScore * 0.1;

        return score;
    }


    protected abstract double worst ();

    protected static void initialize (Board board, int depth)
    {
        MinMaxNode.player = board.getCurrentPlayer ();
        // MinMaxNode.maxDepth = depth;
        // Algo 1
        // MinMaxNode.maxDepth = depth + (int)Math.round(48.0/board.getNbSeeds());

        MinMaxNode.maxDepth = depth + 4 - board.getNbSeeds()/10;
    }

    private int diffScore(Board board)
    {
        return board.getScore (MinMaxNode.player) - board.getScore(Board.otherPlayer (MinMaxNode.player));
    }

    double getEvaluation ()
    {
        return this.evaluation;
    }


    public double [] getDecision ()
    {
        return this.decision;
    }

    protected abstract double alpha (double evaluation, double alpha);


    protected abstract double beta (double evaluation, double beta);


    protected abstract double minmax (double eval1, double eval2);


    protected abstract boolean alphabeta (double eval, double alpha, double beta);


    protected abstract MinMaxNode getNextNode (Board board, int depth, double alpha, double beta);

}
