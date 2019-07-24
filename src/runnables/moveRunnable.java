package runnables;

import controller.Controller;

public class moveRunnable implements Runnable {
    private Controller c;
    public moveRunnable(Controller _c){
        c = _c;
    }

    @Override
    public void run() {
        c.getBoardPanel().getAIButtonm().setEnabled(false);
        c.makeComputerMove();
        c.getBoardPanel().getAIButtonm().setEnabled(true);
    }
}
