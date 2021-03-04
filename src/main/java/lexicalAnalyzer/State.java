package lexicalAnalyzer;

import java.util.HashMap;
import java.util.Map;

public class State {

    private final int curr_state;
    private final Map<Character, Integer> transitions;
    private boolean is_final;
    private String state_name;
    private boolean backtrack;


    public State(int curr_state) {
        this.curr_state = curr_state;
        transitions = new HashMap<>();
    }

    public State(int curr_state, boolean is_final, String state_name, boolean backtrack) {
        this.curr_state = curr_state;
        transitions = new HashMap<>();
        this.is_final = is_final;
        this.state_name = state_name;
        this.backtrack = backtrack;
    }

    public boolean isIs_final() {
        return is_final;
    }

    public void setIs_final(boolean is_final) {
        this.is_final = is_final;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String final_state_name) {
        this.state_name = final_state_name;
    }

    public boolean isBacktrack() {
        return backtrack;
    }

    public void setBacktrack(boolean backtrack) {
        this.backtrack = backtrack;
    }

    public int getCurrState() {
        return curr_state;
    }

    public boolean isFinalState() {
        return is_final;
    }

    public Map<Character, Integer> getTransitions() {
        return transitions;
    }

    public void add_transition(char c, int i) {
        transitions.put(c, i);
    }

}
