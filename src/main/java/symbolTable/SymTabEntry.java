package symbolTable;

import java.util.Vector;

public class SymTabEntry {
    public String          m_kind       = null;
    public String          m_type       = null;
    public String          m_name       = null;
    public int             m_size       = 0;
    public int             m_offset     = 0;
    public SymTab          m_subtable   = null;
    public Vector<String> m_dims       = new Vector<>();
    public Vector<String> m_fParam  = new Vector<String>();

    public String m_visibility  = null;

    public SymTabEntry() {}

    public SymTabEntry(String p_kind, String p_type, String p_name, SymTab p_subtable){
        m_kind = p_kind;
        m_type = p_type;
        m_name = p_name;
        m_subtable = p_subtable;
    }
}
