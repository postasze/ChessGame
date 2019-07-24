package runnables;

import controller.Controller;
import view.BoardPanel;

public class AIThread extends Thread{

    public AIRunnable airunnable;
    Controller controller;

    public AIThread(AIRunnable aiRunnable) {
        super(aiRunnable);
    }

    void setFlag(Boolean _f){
        airunnable.setRunFlag(_f);
    }
}