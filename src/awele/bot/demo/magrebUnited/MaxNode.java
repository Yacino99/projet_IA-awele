package awele.bot.demo.magrebUnited;

import awele.core.Board;

public class MaxNode extends MinMaxNode {

    public MaxNode(Board board) {
        this (board, 0, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public MaxNode(Board board, int depth, double alpha, double beta) {
        super(board, depth, alpha, beta);
    }

    @Override
    protected double worst() {
        return -Double.MAX_VALUE;
    }

    @Override
    protected double alpha(double evaluation, double alpha) {
        return Math.max (evaluation, alpha);
        // PW
        // return Math.min (evaluation, alpha);
    }

    @Override
    protected double beta(double evaluation, double beta) {
        return beta;
    }

    @Override
    protected double minmax(double eval1, double eval2) {
        return Math.max(eval1,eval2);
    }

    @Override
    protected boolean alphabeta(double eval, double alpha, double beta) {
        return eval >= beta;
    }

    @Override
    protected MinMaxNode getNextNode(Board board, int depth, double alpha, double beta) {
        return new MinNode(board, depth, alpha, beta);
    }
}
