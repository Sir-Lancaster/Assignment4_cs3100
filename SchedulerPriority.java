import java.util.PriorityQueue;
import java.util.Queue;

public class SchedulerPriority extends SchedulerBase implements Scheduler {
    private Platform platform;
    private Queue<Process> processQueue;
    private int contextSwitches;

    public SchedulerPriority(Platform platform) {
        this.platform = platform;
        this.processQueue = new PriorityQueue<>((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()));
        this.contextSwitches = 0;
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
        if (cpu == null || cpu.isBurstComplete()) {
            if (cpu != null && cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
                if (!cpu.isExecutionComplete()) {
                    platform.log("Process " + cpu.getName() + " not complete, adding back to queue");
                    contextSwitches++;
                    processQueue.add(cpu);
                } else {
                    platform.log("Process " + cpu.getName() + " execution complete");
                    contextSwitches++;
                }
            }
            if (!processQueue.isEmpty()) {
                Process nextProcess = processQueue.poll();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            } else {
                return null; // No more processes to schedule, return null to indicate idle CPU
            }
        } else if (!processQueue.isEmpty() && processQueue.peek().getPriority() < cpu.getPriority()) {
            platform.log("Preempting: " + cpu.getName() + " with: " + processQueue.peek().getName());
            Process nextProcess = processQueue.poll();
            contextSwitches++;
            processQueue.add(cpu);
            return nextProcess;
        }
        return cpu;
    }
}
