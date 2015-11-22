package wtr.g6;

import wtr.sim.Point;

/**
 * Created by naman on 11/21/15.
 */
public class Person {

    int id;
    // prev thing might not work
    Status prev_status;
    Status cur_status;
    Point prev_position;
    Point cur_position;

    public Person(int id) {
        this.id = id;
        prev_status = Status.stayed;
        cur_status = Status.stayed;
    }

    public void setNewPosition(Point new_position) {
        prev_position = cur_position;
        cur_position = new_position;
    }

    public void setNewStatus(Status new_status) {
        prev_status = cur_status;
        cur_status = new_status;
    }
}
