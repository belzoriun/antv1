package fr.florian.ants.antv1.util.statemachine;

/**
 * Lambda used as machine state's core
 */
public interface MachineStateExecutor {
    void call();
}
