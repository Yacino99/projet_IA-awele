package awele.bot.demo.tcherekMseker;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;


/**
 * @author Alexandre Blansché
 * IA jouant au jouant selon l'algorithme MinMax 
 */
public class MinMaxBot extends DemoBot
{
    /**
     * Constructeur...
     */
    public MinMaxBot ()
    {
        this.setBotName ("TcherekMseker");
        try {
            this.addAuthor("Yacino99");
        } catch (InvalidBotException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void finish() {

    }



    @Override
    public double [] getDecision (Board board)
    {
        /* Si la profondeur de recherche est limitée, il faut réinitaliser la liste de noeuds à chaque décision */
        /* On construit un noeud "max" et on récupère l'évaluation des coups */
        double [] decision = new double[Board.NB_HOLES];
        try {
            decision = new MaxNode(board).getDecision();
        } catch (InvalidBotException e) {
            e.printStackTrace();
        }


        return decision;
    }

    @Override
    public void learn() {

    }
}
