import java.util.PriorityQueue;
import java.util.Queue;

public class SchedulerSJF extends SchedulerBase implements Scheduler {
    private Platform platform;
    private Queue<Process> processesQueue;

    public SchedulerSJF(Platform platform) {
        this.platform = platform;
        this.processesQueue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.getTotalTime(), p2.getTotalTime()));
        this.contextSwitches = 0;
    }
    
    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        processesQueue.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if (cpu ==null || cpu.isBurstComplete()) {
            if (cpu != null && cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
                if (!cpu.isExecutionComplete()) {
                    platform.log("Process " + cpu.getName() + " not complete, adding back to queue");
                    contextSwitches++;
                    processesQueue.add(cpu);
                } else {
                    platform.log("Process " + cpu.getName() + " execution complete");
                    contextSwitches++;
                }
            }
            if (!processesQueue.isEmpty()) {
                Process nextProcess = processesQueue.poll();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            } else {
                return null;
            }
        }
        return cpu;
    }

}
