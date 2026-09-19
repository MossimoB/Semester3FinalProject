package org.mossimo.finalprojectsem3final.app;

import org.mossimo.finalprojectsem3final.model.Difficulty;

/**
 * Shared application state, passed to every screen as it opens
 */
public class AppContext {
    /**
     * The difficulty chosen on the main menu, remembered between screens
     */
    private Difficulty selectedDifficulty = Difficulty.NORMAL;

    /**
     * Navigation set once by Main at startup
     *
     * Controllers navigate through the context rather than each holding
     * their own reference to the Stage
     */
    private SceneManager sceneManager;

    public Difficulty getSelecteDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(Difficulty difficulty) {
        this.selectedDifficulty = difficulty;
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
