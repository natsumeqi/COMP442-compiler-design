package symbolTable;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SymTab {
    public String m_name = null;
    public ArrayList<SymTabEntry> m_symList = null;
    public int m_size = 0;
    public int m_tableLevel = 0;
    public SymTab m_upperTable = null;


    public SymTab(int p_level, SymTab p_uppertable) {
        m_tableLevel = p_level;
        m_name = null;
        m_symList = new ArrayList<SymTabEntry>();
        m_upperTable = p_uppertable;
    }

    public SymTab(int p_level, String p_name, SymTab p_uppertable) {
        m_tableLevel = p_level;
        m_name = p_name;
        m_symList = new ArrayList<SymTabEntry>();
        m_upperTable = p_uppertable;
    }

    public void addEntry(SymTabEntry p_entry) {
        m_symList.add(p_entry);
    }

    public SymTabEntry lookupName(String p_toLookup) {
        SymTabEntry returnvalue = new SymTabEntry();
        boolean found = false;
        for (SymTabEntry rec : m_symList) {
            System.out.println("rec.m_name: "+rec.m_name);
            System.out.println("p_toLookup: "+p_toLookup);
            if (rec.m_name.equals(p_toLookup)) {
                returnvalue = rec;
                found = true;
            }
        }
        if (!found) {
            if (m_upperTable != null) {
                System.out.println("go to upper table for: "+p_toLookup);
                returnvalue = m_upperTable.lookupName(p_toLookup);
            }
        }
        return returnvalue;
    }

    public ArrayList<SymTabEntry> lookupKind(String kind){
         return (ArrayList<SymTabEntry>) m_symList.stream().filter(symTabEntry -> symTabEntry.m_kind.equals(kind)).collect(Collectors.toList());
    }

    public String toString() {
        String stringtoreturn = new String();
        String prelinespacing = new String();
        for (int i = 0; i < this.m_tableLevel; i++)
            prelinespacing += "|    ";
        stringtoreturn += "\n" + prelinespacing + "===================================================================================\n";
        stringtoreturn += prelinespacing + String.format("%-30s", "| table: " + m_name) +
//                String.format("%-27s" , " scope offset: " + m_size) +
                "|\n";
        stringtoreturn += prelinespacing + "=========================================================================\n";
        for (int i = 0; i < m_symList.size(); i++) {
            stringtoreturn += prelinespacing + m_symList.get(i).toString() + '\n';
        }
        stringtoreturn += prelinespacing + "=========================================================================";
        return stringtoreturn;
    }
}
