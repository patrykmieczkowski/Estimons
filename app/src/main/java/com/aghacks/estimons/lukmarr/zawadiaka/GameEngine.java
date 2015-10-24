package com.aghacks.estimons.lukmarr.zawadiaka;

import android.util.Log;
import android.widget.ProgressBar;

import java.util.Random;

/**
 * Created by lukasz on 24.10.15.
 */
public class GameEngine {
    public static final String TAG = GameEngine.class.getSimpleName();
    public static final GameEngine instance = new GameEngine();
    public static boolean gameEnd = false;
    private GameEngine() {
    }

    private static Random randInstance = new Random();
    private static int opponentHp, yourPokeHp;
    private static EndGameListener listener;
    private static ProgressBar barY, barO;

    public static void bindProgressBars(ProgressBar barEstimon, ProgressBar barOpponent) {
        barY = barEstimon;
        barO = barOpponent;
    }

    public static boolean canUserMove = true;

    public static void start(FightActivity fightActivity) {
        Log.d(TAG, "start ");

    }

    public interface EndGameListener {
        void onGameWon();

        void onGameFailed();
    }

    public static int getOpponentHp() {
        return opponentHp;
    }

    public static int getYourPokeHp() {
        return yourPokeHp;
    }

    public static GameEngine clearAll() {
        opponentHp = 100;
        yourPokeHp = 100;
        return instance;
    }

    public static void setup(EndGameListener _listener) {
        listener = _listener;
    }

    public static void hit(boolean isOpponent) {
        Log.d(TAG, "hitOpponent ");
        int j = randInstance.nextInt(4);
        if (j == 2) {
            duzaLepaNaRyj(isOpponent);
        } else {
            malaLepazUcha(isOpponent);
        }
    }

    private static boolean youFailed() {
        return yourPokeHp < 0;
    }

    private static boolean youWon() {
        return opponentHp < 0;
    }

    private static void malaLepazUcha(boolean isOpponent) {
        Log.d(TAG, "malaLepazUcha ");
        int value = randInstance.nextInt(15);
        manageInjury(isOpponent, value);
    }

    private static void duzaLepaNaRyj(boolean isOpponent) {
        Log.d(TAG, "duzaLepaNaRyj ");
        int value = 10 + randInstance.nextInt(30);
        manageInjury(isOpponent, value);
    }

    private static void manageInjury(boolean isOpponent, int value) {
        Log.d(TAG, "manageInjury ");
        if (isOpponent) {
            opponentHp -= value;
            if (opponentHp < 0)
                barO.setProgress(0);
            else
                barO.setProgress(opponentHp);
        } else {
            yourPokeHp -= value;
            if (yourPokeHp < 0)
                barY.setProgress(0);
            else
                barY.setProgress(yourPokeHp);
        }
        if (youFailed()) {
            Log.d(TAG, "game failed");
            listener.onGameFailed();
        } else if (youWon()) {
            Log.d(TAG, "game won");
            listener.onGameWon();
        }
    }
}
