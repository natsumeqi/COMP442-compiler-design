package symbolTable;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SymTab {
    public String m_name = null;
    public ArrayList<SymTabEntry> m_symList = null;
    public int m_size = 0;
    public int m_tableLevel = 0;
    public SymTab m_upperTable = null;


    public ArrayList<SymTab> m_inherit_list = new ArrayList<>();


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

    public void addInherit(SymTab class_table) {
        m_inherit_list.add(class_table);
    }

    public void addEntry(SymTabEntry p_entry) {
        m_symList.add(p_entry);
    }



    public SymTabEntry lookupNameInOneTable(String p_toLookup) {
        SymTabEntry returnvalue = new SymTabEntry();
        for (SymTabEntry rec : m_symList) {
            if (rec.m_name.equals(p_toLookup)) {
                returnvalue = rec;
            }
        }
        return returnvalue;
    }



    public SymTabEntry lookupName(String p_toLookup) {
        SymTabEntry returnvalue = new SymTabEntry();
        boolean found = false;
        for (SymTabEntry rec : m_symList) {
            if (rec.m_name.equals(p_toLookup)) {
                returnvalue = rec;
                found = true;
            }
        }
        if (!found) {
            if (m_upperTable != null) {
                returnvalue = m_upperTable.lookupName(p_toLookup);
            }
        }
        return returnvalue;
    }



    //     for overloading functions
    public ArrayList<SymTabEntry> lookupFunction(String p_func_name) {
        ArrayList<SymTabEntry> returnvalue = new ArrayList<SymTabEntry>();
        boolean found = false;
        for (SymTabEntry rec : m_symList) {
            if (rec.m_name.equals(p_func_name)) {
                returnvalue.add(rec);
                found = true;
            }
        }
        if (!found) {
            if (m_upperTable != null) {
                returnvalue = m_upperTable.lookupFunction(p_func_name);
            }
        }
        return returnvalue;
    }



    //     look variable in inherited class
    public boolean lookupInInherited(String p_var_name) {
        boolean found = false;
        boolean found_in = false;
        for (SymTab rec : m_inherit_list) {
            if (rec != null) {
                if (rec.lookupNameInOneTable(p_var_name).m_name != null) {
                    found = true;
                } else {
                    if (!rec.m_inherit_list.isEmpty()) {
                        found_in = rec.lookupInInherited(p_var_name);
                    }
                }
            }
        }
        return found || found_in;
    }



    //     look variable in inherited class
    public SymTabEntry lookupFunctionInInherited(String p_func_name) {
        SymTabEntry returnvalue = new SymTabEntry();

        for (SymTab rec : m_inherit_list) {
            if (rec != null) {
                if (rec.lookupNameInOneTable(p_func_name).m_name != null) {
                    returnvalue = rec.lookupNameInOneTable(p_func_name);
                } else {
                    if (!rec.m_inherit_list.isEmpty()) {
                        returnvalue = rec.lookupFunctionInInherited(p_func_name);
                    }
                }
            }
        }
        return returnvalue;
    }



    public boolean checkCircular(String class_name){

        boolean found = false;
        boolean found_in = false;
        for (SymTab rec : m_inherit_list) {

            if (rec != null) {
                if (rec.m_name.equals(class_name)) {
                    found = true;
                } else {
                    if (!rec.m_inherit_list.isEmpty()) {
                        found_in = rec.checkCircular(class_name);
                    }
                    // the class table is not created, but the class entry exits
                   for(SymTabEntry entry: rec.m_symList){
                       if(entry.m_kind.equals("inherit"))   {
                           if(entry.m_name.equals(class_name))
                               return true;
                       }
                   }
                }
            }
        }
        return found || found_in;
    }



    public ArrayList<SymTabEntry> lookupKind(String kind) {
        return (ArrayList<SymTabEntry>) m_symList.stream().filter(symTabEntry -> symTabEntry.m_kind.equals(kind)).collect(Collectors.toList());
    }


    public String toString() {
        String stringtoreturn = "";
        String prelinespacing = "";
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
