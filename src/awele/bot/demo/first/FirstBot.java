package awele.bot.demo.first;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui joue toujours le coup le plus à gauche
 */
public class FirstBot extends DemoBot
{
    /**
     * @throws InvalidBotException
     */
    public FirstBot () throws InvalidBotException
    {
        this.setBotName ("First");
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
     * Retourne une valeur décroissante avec l'index du trou
     */
    @Override
    public double [] getDecision (Board board)
    {
        double [] decision = new double [Board.NB_HOLES];
        for (int i = 0; i < decision.length; i++)
            decision [i] = -i;
        return decision;
    }

    /**
     * Pas d'apprentissage
     */
    @Override
    public void learn ()
    {
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
