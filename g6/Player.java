package wtr.g6;

import wtr.sim.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player implements wtr.sim.Player {

	// your own id
	private int self_id = -1;

	// the remaining wisdom per player
	private int[] W = null;
    // map of all people in the room
    Map<Integer, Person> people;

	// random generator
	private Random random = new Random();

	// init function called once
	public void init(int id, int[] friend_ids, int strangers)
	{
        people = new HashMap<Integer, Person>();
		self_id = id;
		// initialize the wisdom array
		int N = friend_ids.length + strangers + 2;
		W = new int [N];
		for (int i = 0 ; i != N ; ++i) {
            W[i] = i == self_id ? 0 : -1;
            people.put(i, new Person(i));
        }
		for (int friend_id : friend_ids)
			W[friend_id] = 50;
	}

	// play function
	public Point play(Point[] players, int[] chat_ids,
	                  boolean wiser, int more_wisdom)
	{
//        System.out.println(players.length);
//        System.out.println(chat_ids.length);
//        System.out.println(Arrays.toString(chat_ids));
//        for(int i = 0;i  < players.length; i++) {
//            if(players[i].id != chat_ids[i]) {
//                System.out.println("Not Same!!");
//            }
//        }
        updatePeople(players, chat_ids);
		// find where you are and who you chat with
		int i = 0, j = 0;
		while (players[i].id != self_id) i++;
		while (players[j].id != chat_ids[i]) j++;
		Point self = players[i];
		Point chat = players[j];
		// record known wisdom
		W[chat.id] = more_wisdom;
		// attempt to continue chatting if there is more wisdom
		if (wiser) return new Point(0.0, 0.0, chat.id);
		// try to initiate chat if previously not chatting
		if (i == j)
			for (Point p : players) {
				// skip if no more wisdom to gain
				if (W[p.id] == 0) continue;
				// compute squared distance
				double dx = self.x - p.x;
				double dy = self.y - p.y;
				double dd = dx * dx + dy * dy;
				// start chatting if in range
				if (dd >= 0.25 && dd <= 4.0)
					return new Point(0.0, 0.0, p.id);
			}
		// return a random move
		double dir = random.nextDouble() * 2 * Math.PI;
		double dx = 6 * Math.cos(dir);
		double dy = 6 * Math.sin(dir);
		return new Point(dx, dy, self_id);
	}

    private void updatePeople(Point[] players, int[] chat_ids) {
        for (int i = 0; i < players.length; i++) {
            Point player = players[i];
            int id = player.id;
            Person person = people.get(id);
            person.setNewPosition(player);
            // if position of the person change, that person is moving
            if (distance(person.prev_position, person.cur_position) != 0) {
                person.setNewStatus(Status.moving);
            } else {
                // if not talking to himself, then talking to someone, otherwise just stayed there
                if(player.id != chat_ids[i]) {
                    person.setNewStatus(Status.talking);
                } else {
                    person.setNewStatus(Status.stayed);
                }
            }
        }
    }

    private double distance(Point p1, Point p2) {
        if(p1 == null || p2 == null) {
            return 0;
        }
        return Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    }
}
