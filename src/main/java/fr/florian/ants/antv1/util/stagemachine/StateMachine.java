package fr.florian.ants.antv1.util.stagemachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateMachine {
    private Map<String, MachineStateExecutor> states;
    private Map<String, Boolean> transitions;
    private Map<StateTransition, String> stateLinks;
    private String currentState;

    public static class StateMachineBuilder
    {
        private Map<String, MachineStateExecutor> states;
        private Map<String, Boolean> transitions;
        private Map<StateTransition, String> stateLinks;
        private String currentState;

        public StateMachineBuilder()
        {
            states = new HashMap<>();
            transitions = new HashMap<>();
            stateLinks = new HashMap<>();
            currentState = null;
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
        if(transitions.containsKey(name))
        {
            for(Map.Entry<String, Boolean> entry : transitions.entrySet())
            {
                if(entry.getKey() == name)
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

    public void step()
    {
        for(Map.Entry<StateTransition, String> entry : stateLinks.entrySet())
        {
            if(entry.getKey().getState() == currentState && transitions.get(entry.getKey().getTransition()))
            {
                currentState = entry.getValue();
                transitions.put(entry.getKey().getTransition(), false);
                break;
            }
        }
        states.get(currentState).call();
    }
}
