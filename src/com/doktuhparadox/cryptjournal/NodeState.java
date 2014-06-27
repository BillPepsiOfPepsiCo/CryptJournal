package com.doktuhparadox.cryptjournal;

import com.doktuhparadox.easel.control.fx.Transitions;

import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Created and written with IntelliJ IDEA.
 * Module of: CryptJournal
 * User: brennanforrest
 * Date of creation: 6/27/14, at 1:17 PM.
 */
public class NodeState {
    public static void enable(Node node) {
        Transitions.fade(node, Duration.millis(700), 0.5, 1.0, false);
    }

    public static void disable(Node node) {
        Transitions.fade(node, Duration.millis(700), 1.0, 0.5, true);
    }
}
