package org.mossimo.finalprojectsem3final.app;

import org.mossimo.finalprojectsem3final.model.Difficulty;
import org.mossimo.finalprojectsem3final.model.Game;

/**
 * Shared application state, passed to every screen as it opens
 */
public class AppContext {

    private Game currentGame;

    /**
     * The difficulty chosen on the main menu, remembered between screens
     */
    private Difficulty selectedDifficulty = Difficulty.NORMAL;

    private double clockSpeed = 1.0;

    private boolean eventsEnabled = true;

    /**
     * Navigation set once by Main at startup
     *
     * Controllers navigate through the context rather than each holding
     * their own reference to the Stage
     */
    private SceneManager sceneManager;

    public Game startNewGame() {
        currentGame = Game.newGame(selectedDifficulty, eventsEnabled);
        return currentGame;
    }

    public Game startSeededGame(long seed) {
        currentGame = Game.seeded(selectedDifficulty, seed, eventsEnabled);
        return currentGame;
    }

    public boolean hasGame() {
        return currentGame != null;
    }

    public Game requireGame() {
        if (currentGame == null) {
            throw new IllegalStateException(
                    "No game in progress. A screen asked for the Game before New Game was pressed.");
        }
        return currentGame;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(Difficulty difficulty) {
        this.selectedDifficulty = difficulty;
    }

    public double getClockSpeed() {
        return clockSpeed;
    }

    public void setClockSpeed(double clockSpeed) {
        this.clockSpeed = clockSpeed;
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Shortcut, so controllers can write {context.show(ScreenId.DASHBOARD)}
     */
    public void show(ScreenId screen) {
        sceneManager.show(screen);
    }
}
