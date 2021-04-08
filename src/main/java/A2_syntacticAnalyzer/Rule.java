package A2_syntacticAnalyzer;

public class Rule {
    private String rule_id;
    private String rule_LHS;
    private String rule_RHS;

    public Rule() {
    }

    public Rule(String rule_id, String rule_LHS, String rule_RHS) {
        this.rule_id = rule_id;
        this.rule_LHS = rule_LHS;
        this.rule_RHS = rule_RHS;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public String getRule_LHS() {
        return rule_LHS;
    }

    public void setRule_LHS(String rule_LHS) {
        this.rule_LHS = rule_LHS;
    }

    public String getRule_RHS() {
        return rule_RHS;
    }

    public void setRule_RHS(String rule_RHS) {
        this.rule_RHS = rule_RHS;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "rule_id='" + rule_id + '\'' +
                ", rule_LHS='" + rule_LHS + '\'' +
                ", rule_RHS='" + rule_RHS + '\'' +
                '}';
    }
}
