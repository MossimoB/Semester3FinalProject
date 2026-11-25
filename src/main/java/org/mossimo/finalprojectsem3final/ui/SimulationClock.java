package org.mossimo.finalprojectsem3final.ui;

import org.mossimo.finalprojectsem3final.model.Simulation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SimulationClock {

    private static final double BASE_SECONDS_PER_TICK = 1.0;

    private final Simulation simulation;
    private final Timeline timeline;

    private ClockSpeed speed = ClockSpeed.NORMAL;

    private Runnable onTick = () -> { };

    private Runnable onFinished = () -> { };

    public SimulationClock(Simulation simulation) {
        this.simulation = simulation;

        this.timeline = new Timeline(new KeyFrame(Duration.seconds(BASE_SECONDS_PER_TICK),
                event -> handleTick()));
        this.timeline.setCycleCount(Animation.INDEFINITE);

        applySpeed();
    }

    private void handleTick() {
        simulation.tick();

        onTick.run();

        if (simulation.isFinished()) {
            stop();
            onFinished.run();
        }
    }

    public void play() {
        if (simulation.isFinished()) {
            return;
        }
        simulation.start();
        timeline.play();
    }

    public void pause() {
        simulation.pause();
        timeline.pause();
    }

    public void toggle() {
        if (isRunning()) {
            pause();
        } else {
            play();
        }
    }

    public void stop() {
        timeline.stop();
        simulation.pause();
    }

    public ClockSpeed cycleSpeed() {
        speed = speed.next();
        applySpeed();
        return speed;
    }

    public void setSpeed(ClockSpeed speed) {
        this.speed = speed;
        applySpeed();
    }

    private void applySpeed() {
        timeline.setRate(speed.getMultiplier());
    }

    public void setOnTick(Runnable onTick) {
        this.onTick = (onTick == null) ? () -> { } : onTick;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = (onFinished == null) ? () -> { } : onFinished;
    }

    public boolean isRunning() {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }

    public ClockSpeed getSpeed() {
        return speed;
    }

    public String getPlayPauseLabel() {
        return isRunning() ? "Pause" : "Play";
    }
}
