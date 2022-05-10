package fr.florian.ants.antv1.util.statemachine;

import fr.florian.ants.antv1.util.Vector;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Class representing a state machine
 */
public class StateMachine {
    private final Map<String, MachineStateExecutor> states;
    private final Map<String, Boolean> transitions;
    private final Map<StateTransition, String> stateLinks;
    private String currentState;

    private String lastState;
    private String lastTransition;

    public String getCurrentState() {
        return currentState;
    }

    public boolean isTransitionSatisfied(String transition)
    {
        if(!transitions.containsKey(transition)) return false;
        return transitions.get(transition);
    }

    public List<String> getStates() {
        return states.keySet().stream().toList();
    }

    public List<String> getTransitions() {
        return transitions.keySet().stream().toList();
    }

    public String getStateToward(String state, String transition) {
        return stateLinks.get(new StateTransition(state, transition));
    }

    /**
     * Class used as machine builder (ensure determinism)
     */
    public static class StateMachineBuilder
    {
        private final Map<String, MachineStateExecutor> states;
        private final Map<String, Boolean> transitions;
        private final Map<StateTransition, String> stateLinks;

        public StateMachineBuilder()
        {
            states = new HashMap<>();
            transitions = new HashMap<>();
            stateLinks = new HashMap<>();
        }

        public StateMachineBuilder addState(String name, MachineStateExecutor executor)
        {
            states.put(name, executor);
            return this;
        }

        public StateMachineBuilder addTransition(String transitionName)
        {
            transitions.put(transitionName, false);
            return this;
        }

        public StateMachineBuilder addStateLink(String fromState, String toState, String transition){
            if(!states.containsKey(fromState))
            {
                System.err.println("State "+fromState+" not found");
                return this;
            }
            if(!states.containsKey(toState))
            {
                System.err.println("State "+toState+" not found");
                return this;
            }
            if(!transitions.containsKey(transition))
            {
                System.err.println("Transition "+transition+" not found");
                return this;
            }
            stateLinks.put(new StateTransition(fromState, transition), toState);
            return this;
        }

        public StateMachine get(String initState) {
            if(!states.containsKey(initState))
            {
                System.err.println("State "+initState+" not found");
                return null;
            }
            return new StateMachine(initState, states, transitions, stateLinks);
        }
    }

    protected StateMachine(String initialState, Map<String, MachineStateExecutor> states, Map<String, Boolean> transitions, Map<StateTransition, String> stateLinks)
    {
        this.states = states;
        this.transitions = transitions;
        this.stateLinks = stateLinks;
        currentState = initialState;
    }

    public void setTransition(String name)
    {
        lastTransition = name;
        if(transitions.containsKey(name))
        {
            for(Map.Entry<String, Boolean> entry : transitions.entrySet())
            {
                if(Objects.equals(entry.getKey(), name))
                {
                    entry.setValue(true);
                }
                else
                {
                    entry.setValue(false);
                }
            }
        }
    }

    /**
     * Check for transitions, move to next state and call its core
     */
    public void step()
    {
        lastState = currentState;
        for(Map.Entry<StateTransition, String> entry : stateLinks.entrySet())
        {
            if(Objects.equals(entry.getKey().getState(), currentState) && transitions.get(entry.getKey().getTransition()))
            {
                currentState = entry.getValue();
                transitions.put(entry.getKey().getTransition(), false);
                break;
            }
        }
        states.get(currentState).call();
    }

    public Image getAsImage()
    {
        double circleSize = 60;
        int circleByLine;
        double rangeBetweenCircles = 80;
        for(circleByLine = 1; circleByLine<10; circleByLine++)
        {
            if(getStates().size()/circleByLine <= circleByLine)
            {
                break;
            }
        }

        double size = circleSize*circleByLine+rangeBetweenCircles*(circleByLine-1)+2;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext context = canvas.getGraphicsContext2D();

        context.save();
        context.translate(1, 1);

        java.util.Map<String, fr.florian.ants.antv1.util.Vector> statePositions = new HashMap<>();
        for(String state : getStates())
        {
            context.setFill(Color.BLACK);
            context.setStroke(Color.BLACK);
            if(Objects.equals(getCurrentState(), state))
            {
                context.setFill(Color.YELLOWGREEN);
            }
            if(lastState == state)
            {
                context.setStroke(Color.GREEN);
            }
            int yindex = getStates().indexOf(state)/circleByLine;
            int xindex = getStates().indexOf(state)%circleByLine;

            double x = xindex * circleSize + xindex*rangeBetweenCircles;
            double y = yindex * circleSize + yindex*rangeBetweenCircles;

            context.strokeOval(x, y, circleSize, circleSize);

            double textX = x+5;
            double v = circleSize / 2 - (state.length() * 6.5) / 2.0;
            if(v >= 0)
            {
                textX += v;
            }
            context.fillText(state, textX, y+circleSize/2+5, circleSize-10);

            statePositions.put(state, new fr.florian.ants.antv1.util.Vector(x+circleSize/2, y+circleSize/2));
        }

        for(java.util.Map.Entry<String, fr.florian.ants.antv1.util.Vector> entry : statePositions.entrySet())
        {
            for(String transition : getTransitions())
            {
                context.setStroke(Color.BLACK);
                context.setFill(Color.BLACK);
                if(transition == lastTransition)
                {
                    context.setStroke(Color.GREEN);
                    context.setFill(Color.GREEN);
                }
                String toward = getStateToward(entry.getKey(), transition);
                if(toward == null)
                {
                    continue;
                }
                fr.florian.ants.antv1.util.Vector towardPos = statePositions.get(toward);
                fr.florian.ants.antv1.util.Vector v = entry.getValue().add(towardPos.multi(-1));
                double angle =v.angle(new Vector(1, 0), true);
                if(towardPos.getY() > entry.getValue().getY())
                {
                    angle = Math.PI*2-angle;
                }
                double aoff = Math.PI/20;
                double xoff1 = Math.cos(angle+aoff)*circleSize/2;
                double yoff1 = Math.sin(angle+aoff)*circleSize/2;
                double xoff2 = Math.cos(angle+Math.PI-aoff)*circleSize/2;
                double yoff2 = Math.sin(angle+Math.PI-aoff)*circleSize/2;

                context.strokeLine(entry.getValue().getX()-xoff1, entry.getValue().getY()-yoff1,
                        towardPos.getX()-xoff2, towardPos.getY()-yoff2);

                double arrowSize = 10;
                double arroffx = Math.cos(angle+Math.PI-Math.PI/6)*arrowSize;
                double arroffy = Math.sin(angle+Math.PI-Math.PI/6)*arrowSize;

                double arroffx1 = Math.cos(angle+Math.PI+Math.PI/6)*arrowSize;
                double arroffy1 = Math.sin(angle+Math.PI+Math.PI/6)*arrowSize;

                Vector left = new Vector(towardPos.getX()-xoff2-arroffx, towardPos.getY()-yoff2-arroffy);
                Vector right = new Vector(towardPos.getX()-xoff2-arroffx1, towardPos.getY()-yoff2-arroffy1);
                context.fillPolygon(new double[]{towardPos.getX()-xoff2, left.getX(), right.getX(), towardPos.getX()-xoff2},
                        new double[]{towardPos.getY()-yoff2, left.getY(), right.getY(), towardPos.getY()-yoff2}, 4);

                int textLength = transition.length();
                double textAngle = angle*180/Math.PI;
                double medX = (towardPos.getX()-xoff2+entry.getValue().getX()-xoff1)/2;
                double medY = (towardPos.getY()-yoff2+entry.getValue().getY()-yoff1)/2;
                context.save();
                if(entry.getValue().getY() < towardPos.getY()) {
                    context.translate(medX, medY);
                }
                else
                {
                    context.translate(medX, medY);
                }

                double offset = -10;
                if(textAngle > 90 && (towardPos.getY() < entry.getValue().getY() || towardPos.getX() > entry.getValue().getX()))
                {
                    textAngle -= 180;
                    offset = 10;
                }
                context.rotate(textAngle);
                context.translate(-textLength*5/2.0, offset);
                context.fillText(transition, 0, 0);
                context.restore();
            }
        }

        context.restore();
        WritableImage image = new WritableImage((int) canvas.getWidth()+1, (int) canvas.getHeight()+1);
        canvas.snapshot(new SnapshotParameters(), image);
        return image;
    }
}
