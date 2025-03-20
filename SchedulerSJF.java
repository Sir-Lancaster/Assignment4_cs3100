
public class SchedulerSJF implements Scheduler {

    public SchedulerSJF(Platform platform) {
    }
    
    @Override
    public int getNumberOfContextSwitches() {
        return 0;
    }

    @Override
    public void notifyNewProcess(Process p) {
        
    }

    @Override
    public Process update(Process cpu) {
        return cpu;
    }

}
