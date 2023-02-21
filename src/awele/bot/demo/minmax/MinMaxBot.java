package awele.bot.demo.minmax;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class MinMaxBot extends DemoBot
{
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 6;
	
    /**
     * @throws InvalidBotException
     */
    public MinMaxBot () throws InvalidBotException
    {
        this.setBotName ("MinMax");
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
     * Pas d'apprentissage
     */
    @Override
    public void learn ()
    {
    }

    /**
     * Sélection du coup selon l'algorithme MinMax
     */
    @Override
    public double [] getDecision (Board board)
    {
        MinMaxNode.initialize (board, MinMaxBot.MAX_DEPTH);
        return new MaxNode (board).getDecision ();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
