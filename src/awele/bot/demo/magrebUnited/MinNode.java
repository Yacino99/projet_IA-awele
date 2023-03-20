package awele.bot.demo.magrebUnited;

import awele.core.Board;

public class MinNode extends MinMaxNode {

    public MinNode(Board board, int depth, double alpha, double beta) {
        super(board, depth, alpha, beta);
    }


    @Override
    protected double worst() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double alpha(double evaluation, double alpha) {
        return alpha;
    }

    @Override
    protected double beta(double evaluation, double beta) {
        return Math.min (evaluation, beta);
    }

    @Override
    protected double minmax(double eval1, double eval2) {
        return Math.min (eval1, eval2);
    }

    @Override
    protected boolean alphabeta(double eval, double alpha, double beta) {
        return eval <= alpha;
    }

    @Override
    protected MinMaxNode getNextNode(Board board, int depth, double alpha, double beta) {
        return new MaxNode(board, depth, alpha, beta);
    }

}
