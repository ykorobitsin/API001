package utils;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Revision Info : $Author$ $Date$
 * Author  : ann
 * Created : Nov 2, 2010 1:32:41 PM
 *
 * @author ann
 */
public final class Actions {

    private static Actions instance;

    private ArrayList actions;

    private Actions() {
        actions = new ArrayList();
    }

    /**
     * Creates instance
     *
     * @return singleton instance
     */
    public static Actions getInstance() {
        if (instance == null) {
            instance = new Actions();
        }
        return instance;
    }

    /**
     * Clears actions list
     */
    public static void clear() {
        getInstance().actions.clear();
    }

    /**
     * Checks if actions list empty
     *
     * @return boolean
     */
    public static boolean isEmpty() {
        //noinspection unchecked
        return getInstance().actions.isEmpty();
    }

    /**
     * Adds list of actions
     *
     * @param objects actions
     */
    public static void addAll(ArrayList<Object> objects) {
        //noinspection unchecked
        getInstance().actions.addAll(objects);
    }

    /**
     * Asserts given actions with actual one
     *
     * @param expectedActions value
     */
    public static void checkActions(Object... expectedActions) {
        checkActionsList(Arrays.asList(expectedActions));
    }

    /**
     * Checks actions list
     *
     * @param expectedActions value
     */
    public static void checkActionsList(List<Object> expectedActions) {
        checkActionsList(expectedActions, false);
    }

    /**
     * Checks given actions with actual one using <b>assertSame</b>
     *
     * @param expectedActions value
     */
    public static void checkActionsSame(Object... expectedActions) {
        checkActionsList(Arrays.asList(expectedActions), true);
    }

    private static void checkActionsList(List<Object> expectedActions, boolean same) {
        ArrayList actualActions = getInstance().actions;

        for (int i = 0; i < expectedActions.size(); ++i) {
            Object expected = expectedActions.get(i);
            Object actual = actualActions.get(i);

            if (expected == null && actual == null) {
                continue;
            }
            if (expected != null
                    && ((same && expected == actual)
                    || (!same && expected.equals(actual)))) {
                continue;
            }
            Assert.fail("Action #" + i + ", expected:<" + expected + "> but was:<" + actual + ">");
        }

        Assert.assertEquals(expectedActions.size(), actualActions.size());

        clear();
    }

    /**
     * Adds action
     *
     * @param params actions
     */
    public static void addAction(Object... params) {
        //noinspection unchecked
        getInstance().actions.addAll(Arrays.asList(params));
    }

    /**
     * Gets action with given index
     *
     * @param i index
     * @return Object
     */
    public static Object get(int i) {
        return getInstance().actions.get(i);
    }

    /**
     * Gets actions count
     *
     * @return value
     */
    public static int count() {
        return getInstance().actions.size();
    }

    /**
     * Checks if actions list contains expected actions
     *
     * @param expectedActions value
     */
    public static void checkActionsContains(Object... expectedActions) {
        List<Object> expectedActionsList = Arrays.asList(expectedActions);
        ArrayList actualActions = getInstance().actions;

        for (Object expectedAction : expectedActionsList) {
            if (!actualActions.contains(expectedAction)) {
                Assert.fail("Expected action <" + expectedAction + "> is not in actual actions :"
                        + actualActions + ".");
            }
        }
    }

    @Override
    public String toString() {
        return "Actions{"
                + "actions=" + actions
                + '}';
    }
}
