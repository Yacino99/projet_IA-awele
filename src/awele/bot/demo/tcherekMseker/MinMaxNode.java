package awele.bot.demo.tcherekMseker;

import java.util.HashMap;

import awele.core.Board;
import awele.core.InvalidBotException;


class Pair<X, Y> {
    public final X x;
    public final Y y;
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
/**
 * @author Alexandre Blansché
 * Classe abstraite pour les noeuds de l'algorithme MinMax
 * Se dérive en deux classes : les noeuds min et les noeuds max...
 */
public abstract class MinMaxNode
{
    /** Coup impossible (case déjà prise) */
    static final int IMPOSSIBLE = -3;
    
    /** Défaite pour l'IA */
    static final int DEFEAT = -2;
    
    /** Non évalué car dépassant la profondeur maximale */
    static final int UKNOWN = -1;
    
    /** Égalité */
    static final int TIE = 0;
    
    /** Victoire pour l'IA */
    static final int WIN = 1;
    
    /** Table de hachage pour stocker les noeuds et éviter des calculs */ 
    private static HashMap <Integer, MinMaxNode> nodes = new HashMap<Integer, MinMaxNode>();
    
    /** Profondeur maximale */
    private static long maxDepth;

    /** L'évaluation du noeud */
    private double evaluation;
    
    /** L'indice du noeud pour la table de hachage */
    private Pair<Integer,Integer> index;

    /** Évaluation des coups selon MinMax */
    private double [] decision;

    /**
     * @param board L'état du plateau de jeu avant de jouer un coup
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     */
    public MinMaxNode (Board board,boolean play, int depth, double alpha, double beta) throws InvalidBotException {
        
        /* On crée un tableau des évaluations des coups à jouer pour chaque situation possible */
        this.decision = new double [board.getPlayerHoles().length];
        for (int i = 0; i < this.decision.length; i++)
                /* On initialise les valeurs : coup IMPOSSIBLE */
                this.decision [i] = MinMaxNode.IMPOSSIBLE;
        /* Initialisation de l'évaluation courante */
        this.evaluation = -Math.signum (this.getBestEvaluation ()) * Double.MAX_VALUE;
        /* Label pour la sortie des boucles imbriquées */
        loop:
            /* On parcourt toutes les coups possibles */
            for (int row = 0; row < board.getPlayerHoles().length; row++)
                /* Si le coup est jouable */
                if (!board.isEmpty (board.getCurrentPlayer()))
                {
                    /* On copie la grille de jeu et on joue le coup sur la copie */
                    Board copy = (Board) board.clone ();

                    /* Si la nouvelle situation de jeu est un coup terminal (victoire du joueur courant)
                     * on affecte une valeur selon le type de noeud */
                    if ( play && (copy.playMoveSimulationBoard(board.getCurrentPlayer(), getDecision()).getPlayerSeeds() > board.playMoveSimulationBoard(Board.otherPlayer(board.getCurrentPlayer()),decision).getOpponentSeeds()))
                        this.decision [row] = this.getBestEvaluation ();
                    /* Sinon, si la grille et pleine (sans gagnant), on a une égalité */


                    /* Sinon, on explore les coups suivants */
                    else
                    {
                        boolean[ ] bv = copy.validMoves(board.getCurrentPlayer());
                        for (int b = 0; b < board.getPlayerHoles().length; b++)
                            if(!bv[b])
                                this.decision [b] = MinMaxNode.TIE;
                        /* Si la profondeur maximale n'est pas atteinte */
                        if (depth < MinMaxNode.maxDepth)
                        {
                            /* On récupère l'indice du nouvel état du plateau de jeu */
                            Pair<Integer,Integer> index = this.getMaxElem (row,copy.getCurrentPlayer(),copy.getPlayerHoles());
                            /* Et on recherche le noeud correspondant dans la liste des noeuds déjà calculés */
                            MinMaxNode child = MinMaxNode.getNode (index.y);
                            /* Si le noeud n'a pas encore été calculé, on le construit */
                            if (child == null)
                                child = this.getNextNode (copy, depth + 1, alpha, beta);
                            /* On récupère l'évaluation du noeud fils */
                            this.decision [row] = child.getEvaluation ();
                        }
                        /* Sinon (si la profondeur maximale est atteinte), on met une évaluation par défaut */
                        else
                            this.decision [row] = MinMaxNode.UKNOWN;
                    }
                    /* L'évaluation courante du noeud est mise à jour, selon le type de noeud (MinNode ou MaxNode) */
                    this.evaluation = this.minmax (this.decision [row], this.evaluation);
                    /* Élagage
                     * Uniquement si on n'est pas sur le noeud initiale
                     * Afin de laisser plusieurs coups jouables pour l'IA */
                    /* Si l'évaluation actuelle est égale à l'optimalité pour le type de noeud, inutile de continuer */
                    if ((depth > 0) && (this.evaluation == this.getBestEvaluation ()))
                        break loop;
                    /* Coupe alpha-beta */
                    if (depth > 0)
                    {
                        if (this.alphabeta (this.evaluation, alpha, beta))
                            break loop;
                        alpha = this.alpha (this.evaluation, alpha);
                        beta = this.beta (this.evaluation, beta);
                    }
                    /* Combinaison des deux méthodes d'élagage */
                    if (depth > 0)
                    {
                        if ((this.evaluation == this.getBestEvaluation ())
                                || (this.alphabeta (this.evaluation, alpha, beta)))
                        break loop;
                        alpha = this.alpha (this.evaluation, alpha);
                        beta = this.beta (this.evaluation, beta);
                    }
                }
    }

    /**
     * Mise à jour de alpha
     * @param evaluation L'évaluation courante du noeud
     * @param alpha L'ancienne valeur d'alpha
     * @return
     */
    protected abstract double alpha (double evaluation, double alpha);

    /**
     * Mise à jour de beta
     * @param evaluation L'évaluation courante du noeud
     * @param beta L'ancienne valeur de beta
     * @return
     */
    protected abstract double beta (double evaluation, double beta);

    /**
     * @return L'indice d'un noeud, calculé en fonction de de la situation de la grille de jeu
     */
    public Pair<Integer,Integer> getIndex ()
    {
        return this.index;
    }

    /**
     * Récupération d'un noeud déjà calculé 
     * @param index L'indice du noeud
     * @return Le noeud qui a l'indice indiqué ou null s'il n'a pas encore été calculé
     */
    public static MinMaxNode getNode (long index)
    {
        return MinMaxNode.nodes.get (index);
    }

    /**
     * @return Le nombre de noeuds calculées
     */
    public static int getNbNodes ()
    {
        return MinMaxNode.nodes.size ();
    }

    /**
     * Initialise la liste de noeuds (liste vide)
     */
    protected static void initialize (Board board)
    {
        MinMaxNode.nodes = new HashMap <Integer, MinMaxNode> ();
        MinMaxNode.maxDepth = Math.max (1, 12 - Math.max (board.getPlayerHoles()[0],board.getOpponentHoles()[0]));
        for(int i =0; i < 6 ; i++)
            MinMaxNode.maxDepth = Math.max (1, 12 - Math.max(MinMaxNode.maxDepth,Math.max (board.getPlayerHoles()[i],board.getOpponentHoles()[i])));
    }

    /**
     * Évaluation optimale pour le noeud courant (DEFEAT pour un noeud MinNode, WIN pour un noeud MaxNode)
     * @return
     */
    protected abstract double getBestEvaluation ();

    /**
     * Retourne le min ou la max entre deux valeurs, selon le type de noeud (MinNode ou MaxNode)
     * @param eval1 Un double
     * @param eval2 Un autre double
     * @return Le min ou la max entre deux valeurs, selon le type de noeud
     */
    protected abstract double minmax (double eval1, double eval2);

    /**
     * Indique s'il faut faire une coupe alpha-beta, selon le type de noeud (MinNode ou MaxNode)
     * @param eval L'évaluation courante du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un booléen qui indique s'il faut faire une coupe alpha-beta
     */
    protected abstract boolean alphabeta (double eval, double alpha, double beta);

    /**
     * Retourne un noeud (MinNode ou MaxNode) du niveau suivant
     * @param board L'état de la grille de jeu
     * @param depth La profondeur du noeud
     * @param alpha Le seuil pour la coupe alpha
     * @param beta Le seuil pour la coupe beta
     * @return Un noeud (MinNode ou MaxNode) du niveau suivant
     */
    protected abstract MinMaxNode getNextNode (Board board, int depth, double alpha, double beta) throws InvalidBotException;

    /**
     * L'évaluation du noeud
     * @return L'évaluation du noeud
     */
    double getEvaluation ()
    {
        return this.evaluation;
    }

    /**
     * L'évaluation de chaque coup possible pour le noeud
     * @return
     */
    double [] getDecision ()
    {
        return this.decision;
    }

    private Pair<Integer,Integer> getMaxElem(int currentHole,int player,int[] holes){
        int ind = currentHole;
        int maxi = holes[ind];
        for(int i = 0; i < Board.NB_HOLES ; i++){
            if(holes[i] > maxi && i != currentHole){
                ind = i;
                maxi = holes[ind];
            }
        }
        return new Pair<Integer,Integer>(ind,maxi);
    }
}
