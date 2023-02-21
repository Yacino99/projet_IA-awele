package awele.bot.demo.minmax;

import awele.core.Board;

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
    MaxNode (Board board)
    {
        this (board, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Constructeur d'un noeud interne
     * @param board La situation de jeu pour le noeud
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha-beta
     * @param beta Le seuil pour la coupe alpha-beta
     */
    MaxNode (Board board, int depth, double alpha, double beta)
    {
        super (board, depth, alpha, beta);
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
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un noeud MinNode du niveau suivant
     */
    @Override
    protected MinMaxNode getNextNode (Board board, int depth, double alpha, double beta)
    {
        return new MinNode (board, depth, alpha, beta);
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

    /** Pire score : une petite valeur */
    @Override
    protected double worst ()
    {
        return -Double.MAX_VALUE;
    }
}
