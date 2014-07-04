package com.doktuhparadox.cryptjournal.util;

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

    /**
     * Fades a node in and enables it.
     *
     * @param node the node to fade in and enable.
     */
    public static void enable(Node node) {
        Transitions.fade(node, Duration.millis(300), 0.5, 1.0, false);
    }

    /**
     * Fades a node out and disables it.
     *
     * @param node the node to fade out and disable.
     */
    public static void disable(Node node) {
        Transitions.fade(node, Duration.millis(300), 1.0, 0.5, true);
    }
}
