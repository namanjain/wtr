package wtr.g6;

import wtr.sim.Point;

/**
 * Created by naman on 11/21/15.
 */
public class Person {

    int id;
    Status prev_turn;
    Status cur_turn;
    Point prev_position;
    Point cur_position;

    public Person(int id) {
        this.id = id;
        prev_turn = Status.stayed;
        cur_turn = Status.stayed;
    }

    public void setNewPosition(Point position) {
        prev_position = cur_position;
        cur_position = position;
    }
}
