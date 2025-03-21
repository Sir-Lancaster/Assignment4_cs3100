import java.util.LinkedList;
import java.util.Queue;

public class SchedulerRR extends SchedulerBase implements Scheduler {
    private Platform platform;
    private Queue<Process> processQueue;
    private int timeQuantum;
    private int contextSwitches;
    private int burstCounter; // Custom burst counter

    public SchedulerRR(Platform platform, int timeQuantum) {
        this.platform = platform;
        this.processQueue = new LinkedList<>();
        this.timeQuantum = timeQuantum;
        this.contextSwitches = 0;
        this.burstCounter = 0; // Initialize burst counter
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
        // Increment burst counter at the beginning
        if (cpu != null) {
            burstCounter++;
        }

        if (cpu == null || cpu.isBurstComplete() || burstCounter >= timeQuantum) {
            if (cpu != null && cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
                if (!cpu.isExecutionComplete()) {
                    platform.log("Process " + cpu.getName() + " not complete, adding back to queue");
                    processQueue.add(cpu);
                    contextSwitches++;
                } else {
                    platform.log("Process " + cpu.getName() + " execution complete");
                    contextSwitches++;
                }
            } else if (cpu != null && burstCounter >= timeQuantum) {
                platform.log("Time quantum complete for process " + cpu.getName());
                contextSwitches++;
                processQueue.add(cpu);
            }
            burstCounter = 0; // Reset burst counter
            if (!processQueue.isEmpty()) {
                Process nextProcess = processQueue.poll();
                platform.log("Scheduled " + nextProcess.getName());
                contextSwitches++;
                return nextProcess;
            } else {
                return null;
            }
        }
        return cpu;
    }
}
