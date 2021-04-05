package symbolTable;

import java.util.Vector;

public class FuncEntry extends SymTabEntry {

    public int m_line;

    public FuncEntry(String p_type, String p_name, Vector<String> p_fParam, SymTab p_table, int p_line){
        super("function", p_type, p_name, p_table);
        m_fParam = p_fParam;
        m_line = p_line;
    }

    public FuncEntry(String p_type, String p_name,  Vector<String> p_fParam, String p_visibility, int p_line){
        super("function", p_type, p_name, null);
        m_visibility = p_visibility;
        m_fParam = p_fParam;
        m_line = p_line;
    }

    public String toString(){
        if(m_visibility==null){
            return 	String.format("%-12s" , "| " + m_kind) +
                    String.format("%-16s" , "| " + m_name) +
                    String.format("%-40s"  , "| " + "("+m_fParam.toString().substring(1,m_fParam.toString().length()-1)+"):"+m_type) +
                    m_subtable +
                    // String.format("%-12"  , "| " + m_dims) +
//                String.format("%-8s"  , "| " + m_size) +
//                String.format("%-8s"  , "| " + m_offset)
                    "|";
        }else{
            return 	String.format("%-12s" , "| " + m_kind) +
                    String.format("%-16s" , "| " + m_name) +
                    String.format("%-40s"  , "| " + "("+m_fParam.toString().substring(1,m_fParam.toString().length()-1)+"):"+m_type) +
                    String.format("%-12s" , "| " + m_visibility) +
                    m_subtable +
                    // String.format("%-12"  , "| " + m_dims) +
//                String.format("%-8s"  , "| " + m_size) +
//                String.format("%-8s"  , "| " + m_offset)
                    "|";
        }
    }



}
