package agaex.otrs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import agaex.otrs.json.JSON;
import agaex.otrs.layouts.Ticket;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Queue extends Activity {
	
	private Ticket[] tickets;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.queue);
		
		Bundle bundle = getIntent().getExtras();
		final String queue_id = bundle.getString("QUEUE");
		
		String cadena_conexion = ((Otrs)getApplicationContext()).getUrl();
		cadena_conexion += "&Method=QueueView&Data=%7B%22QueueID%22:"+queue_id+"%7D";
		
		TextView txtQueue = (TextView) findViewById(R.id.txtQueue);
		txtQueue.setText(bundle.getString("QUEUE_NAME"));
		
		ListView lstTicket = (ListView) findViewById(R.id.lstTicket);
		
		JSON json = new JSON(cadena_conexion);
		String success = json.getResult();
		
		final JSONArray tickets_json = json.getJson_array();
		
		if (!success.equals("successful")){
		
			Toast.makeText(Queue.this, "Ha ocurrido un error.", Toast.LENGTH_LONG).show();
			return;
			
		}
		
		tickets = new Ticket[tickets_json.length()];
        
        for (int i = 0; i < tickets_json.length(); i++){
        	try{
        		//tickets[i] = ((JSONObject)tickets_json.get(i)).getString("Subject").substring(1, 25);
        		tickets[i] = new Ticket(((JSONObject)tickets_json.get(i)).getString("Subject"), 
        								((JSONObject)tickets_json.get(i)).getString("TicketNumber"),
        								((JSONObject)tickets_json.get(i)).getString("From"),
        								((JSONObject)tickets_json.get(i)).getString("Age"),
        								((JSONObject)tickets_json.get(i)).getString("State"),
        								((JSONObject)tickets_json.get(i)).getString("Queue"),
        								((JSONObject)tickets_json.get(i)).getString("PriorityID"),
        								((JSONObject)tickets_json.get(i)).getString("PriorityColor"),
        								((JSONObject)tickets_json.get(i)).getString("TicketID"),
        								((JSONObject)tickets_json.get(i)).getString("TicketID"),
        								((JSONObject)tickets_json.get(i)).getString("TicketID"),
        								((JSONObject)tickets_json.get(i)).getString("Owner"));
        	}catch (JSONException e) {
        		Toast.makeText(Queue.this, "Ha ocurrido un error. "+e.getMessage(), Toast.LENGTH_LONG).show();
    			return;
			}
        }
        
        AdaptadorTicket adaptador = new AdaptadorTicket(this);
        lstTicket.setAdapter(adaptador);
        
        lstTicket.setOnItemClickListener(new OnItemClickListener() {
        
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				
				Intent intent = new Intent(Queue.this, agaex.otrs.Ticket.class);
				Bundle bundle = new Bundle();
				
				bundle.putString("TICKETID", tickets[position].getTicketID());
				bundle.putString("TICKETNUMBER", tickets[position].getTicketNumber());
				bundle.putString("TICKETSUBJECT", tickets[position].getSubject());
				bundle.putString("TICKETAGE", tickets[position].getAge());
				bundle.putString("TICKETTYPE", tickets[position].getTicketID());
				bundle.putString("TICKETSTATE", tickets[position].getState());
				bundle.putString("TICKETPRIORITY", tickets[position].getPriority());
				bundle.putString("TICKETQUEUE", tickets[position].getQueue());
				bundle.putString("TICKETCUSTOMER", tickets[position].getCustomer());
				bundle.putString("TICKETOWNER", tickets[position].getOwner());
				bundle.putString("TICKETRESPONSIBLE", tickets[position].getResponsible());
				
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
        	
        });
        
	}
	
	class AdaptadorTicket extends ArrayAdapter {
	
		Activity context;
		private int[] colors = new int[] {Color.WHITE, Color.GRAY};
		String backColor;
		int height = 0;
		
		public AdaptadorTicket(Activity context) {
			super(context, R.layout.ticket_list, tickets);
			this.context = context;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
		
			LayoutInflater inflater = context.getLayoutInflater();
			View item = new View(context);
			try{
				item = inflater.inflate(R.layout.ticket_list, null);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			int colorPos = position % colors.length;
			
			TextView ticketSubject = (TextView) item.findViewById(R.id.ticketSubject);
			ticketSubject.setText(tickets[position].getSubject());
			
			TextView ticketNumber = (TextView) item.findViewById(R.id.ticketNumber);
			ticketNumber.setText(tickets[position].getTicketNumber());
			
			TextView ticketFrom = (TextView) item.findViewById(R.id.ticketFrom);
			ticketFrom.setText(tickets[position].getFrom());
			
			TextView ticketAge = (TextView) item.findViewById(R.id.ticketAge);
			ticketAge.setText(tickets[position].getAge());
			
			TextView ticketState = (TextView) item.findViewById(R.id.ticketState);
			ticketState.setText(tickets[position].getState());
			
			TextView ticketQueue = (TextView) item.findViewById(R.id.ticketQueue);
			ticketQueue.setText(tickets[position].getQueue());
			
			View priorityColor = (View) item.findViewById(R.id.ticketPrioColor);
			backColor = tickets[position].getPriorityColor();
			
			priorityColor.setBackgroundColor(Color.parseColor(backColor));
			item.setBackgroundColor(colors[colorPos]);
			
			return (item);
		}
		
	}
}