package awele.bot.demo.magrebUnited;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

public class MagrebBot extends DemoBot {

    private static final int DEPTH = 7;


    public MagrebBot() throws InvalidBotException {
        this.setBotName ("MagrebBot");
        this.addAuthor ("Salim Boutament & Yacine Seddar-Yagoub");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void finish() {

    }

    @Override
    public double[] getDecision(Board board) {
        MinMaxNode.initialize (board, MagrebBot.DEPTH);
        return new MaxNode(board).getDecision();
    }

    @Override
    public void learn() {

    }
}