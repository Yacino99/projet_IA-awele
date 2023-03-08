package awele.bot.demo.tcherekMseker;

import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Noeud Max : estimation du meilleur coup possible pour l'IA
 */
public class MaxNode extends MinMaxNode
{
    /**
     * Constructeur pour un noeud initial
     * @param board La situation de jeu pour laquelle il faut prendre une décision
     */
    MaxNode (Board board) throws InvalidBotException {
        this (board, true, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Constructeur d'un noeud interne
     * @param board La situation de jeu pour le noeud
     * @param depth La profondeur du noeud
     * param alphabeta Le seuil pour la coupe alpha-beta
     */
    MaxNode (Board board, boolean play, int depth, double alpha, double beta) throws InvalidBotException {
        super (board,play, depth, alpha, beta);
    }

    /**
     * Évaluation optimale pour le noeud courant : WIN
     * @return L'évaluation optimale pour le noeud courant : WIN
     */
    @Override
    protected double getBestEvaluation ()
    {
        return MinMaxNode.WIN;
    }

    /**
     * Retourne le max
     * @param eval1 Un double
     * @param eval2 Un autre double
     * @return Le max entre deux valeurs, selon le type de noeud
     */
    @Override
    protected double minmax (double eval1, double eval2)
    {
        return Math.max (eval1, eval2);
    }

    /**
     * Indique s'il faut faire une coupe alpha-beta
     * (si l'évaluation courante du noeud est supérieure à l'évaluation courante du noeud parent)
     * @param eval L'évaluation courante du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un booléen qui indique s'il faut faire une coupe alpha-beta
     */
    @Override
    protected boolean alphabeta (double eval, double alpha, double beta)
    {
        return eval >= beta;
    }

    /**
     * Retourne un noeud MinNode du niveau suivant
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * param alphabeta Le seuil pour la coupe alpha-beta
     * @return Un noeud MinNode du niveau suivant
     */
    @Override
    protected MinMaxNode getNextNode (Board board, int depth, double alpha, double beta) throws InvalidBotException {
        return new MinNode (board, false, depth, alpha, beta);
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
        return Math.max (evaluation, alpha);
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
        return beta;
    }
}
