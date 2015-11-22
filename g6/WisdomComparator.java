package wtr.g6;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class WisdomComparator implements Comparator<Person>{

	// In the future, add more attributes here as we make this logic more sophisticated!

    @Override
    public int compare(Person p1, Person p2) {
        return p1.wisdom > p2.wisdom ? -1 : 1;
    }
}