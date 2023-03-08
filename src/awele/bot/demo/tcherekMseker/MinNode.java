package awele.bot.demo.tcherekMseker;

import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Noeud Min : estimation du meilleur coup possible pour l'adversaire
 */
public class MinNode extends MinMaxNode
{
    /**
     * Constructeur pour un noeud initial
     * @param board La situation de jeu pour laquelle il faut prendre une décision
     */
    MinNode (Board board, long ind) throws InvalidBotException {
        this (board,false,ind, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Constructeur d'un noeud interne
     * @param board La situation de jeu pour le noeud
     * @param depth La profondeur du noeud
     * param alphabeta Le seuil pour la coupe alpha-beta
     */
    MinNode (Board board, boolean play, long ind, int depth, double alpha, double beta) throws InvalidBotException {
        super (board,play, ind, depth, alpha, beta);
    }

    /**
     * Évaluation optimale pour le noeud courant : DEFEAT
     * @return L'évaluation optimale pour le noeud courant : DEFEAT
     */
    @Override
    protected double getBestEvaluation ()
    {
        return MinMaxNode.DEFEAT;
    }

    /**
     * Retourne le min
     * @param eval1 Un double
     * @param eval2 Un autre double
     * @return Le min entre deux valeurs, selon le type de noeud
     */
    @Override
    protected double minmax (double eval1, double eval2)
    {
        return Math.min (eval1, eval2);
    }

    /**
     * Indique s'il faut faire une coupe alpha-beta
     * (si l'évaluation courante du noeud est inférieure à l'évaluation courante du noeud parent)
     * @param eval L'évaluation courante du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un booléen qui indique s'il faut faire une coupe alpha-beta
     */
    @Override
    protected boolean alphabeta (double eval, double alpha, double beta)
    {
        return eval <= alpha;
    }

    /**
     * Retourne un noeud MaxNode du niveau suivant
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * param alphabeta Le seuil pour la coupe alpha-beta
     * @return Un noeud MaxNode du niveau suivant
     */
    @Override
    protected MinMaxNode getNextNode (Board board,Long ind, int depth, double alpha, double beta) throws InvalidBotException {
        return new MaxNode (board, true,ind, depth, alpha, beta);
    }

    /**
     * Mise à jour de alpha
     * @param evaluation L'évaluation courante du noeud
     * @param alpha L'ancienne valeur d'alpha
     * @return
     */
    @Override
    protected double alpha (double evaluation, double alpha)
    {
        return alpha;
    }

    /**
     * Mise à jour de beta
     * @param evaluation L'évaluation courante du noeud
     * @param beta L'ancienne valeur de beta
     * @return
     */
    @Override
    protected double beta (double evaluation, double beta)
    {
        return Math.min (evaluation, beta);
    }
}
