package fr.florian.ants.antv1.util;

import fr.florian.ants.antv1.living.ant.Ant;

public class AntMoveAnimator{
    private Ant toAnimate;
    private Direction goTo;
    private double moveSpan;

    public AntMoveAnimator(Ant a, Direction dir)
    {
        toAnimate = a;
        goTo = dir;
        moveSpan = 1.0/GameTimer.getInstance().getTickTime();
    }

    public void makeAnimation()
    {

    }
}
