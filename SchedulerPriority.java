import java.util.PriorityQueue;
import java.util.Queue;

public class SchedulerPriority extends SchedulerBase implements Scheduler {
    private Platform platform;
    private Queue<Process> processQueue;
    public SchedulerPriority(Platform platform) {
        this.platform = platform;
        this.processQueue = new PriorityQueue<Process>((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()));
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
            if (cpu !=null && cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
            }
            if (!processQueue.isEmpty()) {
                Process nextProcess = processQueue.poll();
                platform.log("Scheduled: " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            }
            else {
                if (!processQueue.isEmpty() && processQueue.peek().getPriority() < cpu.getPriority()) {
                    platform.log("Scheduled: " + processQueue.peek().getName());
                    contextSwitches++;
                    return processQueue.poll();
                }
            }
        }
        return cpu;
    }

}
