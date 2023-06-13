package mchorse.bbs.game.utils.executables;

import mchorse.bbs.core.ITickable;

import java.util.ArrayList;
import java.util.List;

public class Executables implements ITickable
{
    /**
     * Delayed executions
     */
    private List<IExecutable> executables = new ArrayList<IExecutable>();

    /**
     * Second executables list to avoid concurrent modification
     * exceptions when adding consequent delayed executions
     */
    private List<IExecutable> secondList = new ArrayList<IExecutable>();

    public void add(List<IExecutable> executionForks)
    {
        this.executables.addAll(executionForks);
    }

    public void add(IExecutable executable)
    {
        this.executables.add(executable);
    }

    @Override
    public void update()
    {
        if (!this.executables.isEmpty())
        {
            /* Copy original event forks to another list and clear them
             * to be ready for new forks */
            this.secondList.addAll(this.executables);
            this.executables.clear();

            /* Execute event forks (and remove those which were finished) */
            this.secondList.removeIf(IExecutable::update);

            /* Add back to the original list the remaining forks and
             * new forks that were added by consequent executions */
            this.secondList.addAll(this.executables);
            this.executables.clear();
            this.executables.addAll(this.secondList);
            this.secondList.clear();
        }
    }
}