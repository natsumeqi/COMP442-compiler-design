package symbolTable;


import java.util.HashMap;

public class ClassEntry extends SymTabEntry {

    public ClassEntry(String p_name, SymTab p_subtable){
        super(new String("class"), p_name, p_name, p_subtable);
    }

//    @Override
//    public void setInherit_list(HashMap<String, SymTab> inherit_list) {
//        super.setInherit_list(inherit_list);
//    }

    public String toString(){
        return 	String.format("%-12s" , "| " + m_kind) +
                String.format("%-40s" , "| " + m_name) +
                "|" +
                m_subtable;
    }

}

