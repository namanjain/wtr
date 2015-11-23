package wtr.g6;

import wtr.sim.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.Iterator;



public class Player implements wtr.sim.Player {

	// your own id
	private int self_id = -1;

	// the remaining wisdom per player
	private int[] W = null;
    // map of all people in the room
    Map<Integer, Person> people;

	// random generator
	private Random random = new Random();

	PriorityQueue<Person> maximum_wisdom_queue;
	int[] spoken; //0 = not spoken, 1=hello, 2 = zero wisdom left

	int count = 0;

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

		spoken = new int[N];
		Arrays.fill(spoken, 0); //0 = not spoken, 1=hello, 2 = zero wisdom left

		maximum_wisdom_queue = new PriorityQueue<Person>(new WisdomComparator());
	}


/*
	// old play function
	public Point play(Point[] players, int[] chat_ids,
	                  boolean wiser, int more_wisdom)
	{

        updatePeople(players, chat_ids);
		// find where you are and who you chat with
		int i = 0, j = 0;
		while (players[i].id != self_id) i++; // Find myself in the players array.
		while (players[j].id != chat_ids[i]) j++; // Find my chat-buddy (who I'm currently talking to)
		Point self = players[i];
		Point chat = players[j];
		// record known wisdom
		W[chat.id] = more_wisdom;
		System.out.println("Player :: "+self.id+" talking to ::"+chat.id);
		// attempt to continue chatting if there is more wisdom
		if (wiser) return new Point(0.0, 0.0, chat.id);
		// try to initiate chat if previous turn I not chatting not chatting with anyone
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
*/

	// play function
	public Point play(Point[] players, int[] chat_ids,
	                  boolean wiser, int more_wisdom)
	{

        updatePeople(players, chat_ids);
		// find where you are and who you chat with
		int i = 0, j = 0;
		while (players[i].id != self_id) i++; // Find myself in the players array.
		while (players[j].id != chat_ids[i]) j++; // Find my chat-buddy (who I'm currently talking to)
		Point self = players[i];
		Point chat = players[j];
		
		W[chat.id] = more_wisdom; // record known wisdom
		maximum_wisdom_queue.add(new Person(chat.id, more_wisdom));
		System.out.println("--------------------------------");
		System.out.println("adding person " + chat.id);
		System.out.println("--------------------------------");
		System.out.println("Player "+self.id+" now talking to "+chat.id);
		spoken[chat.id] = wiser==true? 1:2; //wiser = more wisdom left

		//Say hello!
		for (Point p : players) {
			// Skip if you've already said hello!
			if (spoken[p.id] != 0) 
			{
				continue;
			}

			
			// Say hello if in range & not spoken to earlier!
			if(inTalkRange(self, p))
			{	
				System.out.println(self.id + " trying to saying hello to "+p.id);
				//maximum_wisdom_queue.add(new Person(p.id, more_wisdom));
				return new Point(0.0, 0.0, p.id);
			}

		/* TO DO: if player no longer in range, then pop out player from queue?*/
		}
		
		
		/*
		Person p = maximum_wisdom_queue.peek();
		System.out.println("--------------------------------");
		System.out.println("p.id: " + p.id);
		System.out.println("chat_ids: ");
		int k=0;
		for(; k<chat_ids.length; k++) 
		{
			if(chat_ids[k] == p.id) break;
			System.out.println(chat_ids[k]);
		}
		System.out.println("--------------------------------");
		if(k >= chat_ids.length) System.exit(-1);
		*/
		
		// exhaust the person with the max wisdom		
		Person[] person_by_w = new Person[maximum_wisdom_queue.size()];
		int pi = 0;
		for(Person ps : maximum_wisdom_queue)
		{
			person_by_w[pi++] = ps;
		}
		Arrays.sort(person_by_w, maximum_wisdom_queue.comparator());
		int k=0;
		for(; k<person_by_w.length; k++)
		{
			//System.out.println("---aaaaaaaaaaaa-------aaaaaaaaaaaaaaaaaa----------------------");
			int idx = 0;
			// find the actual player with the max wisdom
			while (idx<players.length && players[idx].id != person_by_w[k].id ) idx++;
			// if we can't see the max wisdom person any more or he is out of talking range then pick up the next...
			if(idx >= players.length || !inTalkRange(self, players[idx]))
				continue;
			//System.out.println("---aaaaaaaaaaaa-------aaaaaaaaaaaaaaaaaa----------------------");
			// else find out who is the max person talking to
			int idx2 = 0;
			while (idx2<chat_ids.length && chat_ids[idx2] != person_by_w[k].id ) idx2++;
			// if he is talking to some one else, then skip
			//System.out.println("idx2: " + idx2 + ", chat_ids.length: "+chat_ids.length+", person_by_w[k].id"+person_by_w[k].id);
			if(idx2 == self.id || idx2 == person_by_w[k].id)
			{
				System.out.println("I, " + self.id + ", am talking to "+person_by_w[k].id);
				//System.exit(-1);
				return new Point(0.0, 0.0, person_by_w[k].id);
			}
		}
		
		
		

		///// else: move to some where else //////
		
		
		
		
		// return a random move
		System.out.println("Random move!");
		double dir = random.nextDouble() * 2 * Math.PI;
		double dx = 6 * Math.cos(dir);
		double dy = 6 * Math.sin(dir);
		return new Point(dx, dy, self_id);
	}
	
	
	
	
	private boolean inTalkRange(Point self, Point p)
	{
		double distance = Utils.distance(self, p);
		if (distance >= 0.5 && distance <= 2.0) return true;
		return false;
	}

    private void updatePeople(Point[] players, int[] chat_ids) {
        for (int i = 0; i < players.length; i++) {
            Point player = players[i];
            int id = player.id;
            Person person = people.get(id);
            person.setNewPosition(player);
            // if position of the person change, that person is moving
            if (Utils.distance(person.prev_position, person.cur_position) != 0) {
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


    public void debug_queue(PriorityQueue<Person> maximum_wisdom_queue)
    {
    	if (count++ == 100)
		{	System.out.println("----------------->"+count);
			while(!maximum_wisdom_queue.isEmpty()){
				Person p = maximum_wisdom_queue.poll();
				System.out.println(p.id+" | "+p.wisdom);}
			System.out.println("----------------->"+count);
		}
    }

}
