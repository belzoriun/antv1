package fr.florian.ants.antv1.util.statemachine;

import java.util.Objects;

public record StateTransition(String state, String transition) {

    public String getState() {
        return state;
    }

    public String getTransition() {
        return transition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateTransition that = (StateTransition) o;
        return state.equals(that.state) && transition.equals(that.transition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, transition);
    }
}
