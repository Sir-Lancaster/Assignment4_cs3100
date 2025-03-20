import java.util.LinkedList;
import java.util.Queue;

public class SchedulerRR extends SchedulerBase implements Scheduler { 
    private Platform platform;
    private Queue<Process> processQueue;
    private int timeQuantum;
    public SchedulerRR(Platform platform, int timeQuantum) {
        this.platform = platform;
        this.processQueue = new LinkedList<Process>();
        this.timeQuantum = timeQuantum;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processQueue.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu == null || cpu.isBurstComplete() || cpu.getElapsedTotal() >= timeQuantum) {
            if (cpu != null && !cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
            } else if (cpu != null && cpu.getElapsedTotal() >= timeQuantum) {
                platform.log("Time quantum complete for " + cpu.getName());
                processQueue.add(cpu);
            }
            if (!processQueue.isEmpty()) {
                Process nextProcess = processQueue.poll();
                platform.log("Scheduled " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            }
            
        }
        return cpu;
    }

}
