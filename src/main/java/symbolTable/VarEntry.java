package symbolTable;

import java.util.Vector;

public class VarEntry extends SymTabEntry {


    public VarEntry(String p_kind, String p_type, String p_name, Vector<String> p_dims) {
        super(p_kind, p_type, p_name, null);
        m_dims = p_dims;
    }

    public VarEntry(String p_kind, String p_type, String p_name, String p_visibility, Vector<String> p_dims) {
        super(p_kind, p_type, p_name, null);
        m_visibility = p_visibility;
        m_dims = p_dims;
    }


    public String toString() {
        String dim_print;
        if (m_dims != null) {
             dim_print = m_dims.toString().substring(1, m_dims.toString().length() - 1);
        }else{
            dim_print="";
        }

        if (m_visibility == null) {
            return String.format("%-12s", "| " + m_kind) +
                    String.format("%-16s", "| " + m_name) +
                    String.format("%-40s", "| " + m_type + dim_print)
                    //String.format("%-12"  , "| " + m_dims) +
//                String.format("%-8s"  , "| " + m_size) +
//                String.format("%-8s"  , "| " + m_offset)
                    + "|"
                    ;
        } else {
            return String.format("%-12s", "| " + m_kind) +
                    String.format("%-16s", "| " + m_name) +
                    String.format("%-40s", "| " + m_type + dim_print) +
                    String.format("%-12s", "| " + m_visibility)
                    //String.format("%-12"  , "| " + m_dims) +
//                String.format("%-8s"  , "| " + m_size) +
//                String.format("%-8s"  , "| " + m_offset)
                    + "|"
                    ;
        }

    }
}
