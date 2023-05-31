package Index;

import java.util.HashSet;

public class gridMatch {
    HashSet<Integer> set;
    Integer ID;

    public gridMatch( Integer ID,HashSet<Integer> set) {
        this.set = set;
        this.ID = ID;
    }

    public HashSet<Integer> getSet() {
        return set;
    }

    public Integer getID() {
        return ID;
    }
}
