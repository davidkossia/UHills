package server;

public class Item {
	private String name;
	private double buyNow;
	private double start;
	private double bid;
	private boolean sold;
	String description;
	String topBid;
	
	public Item(String name, double bn, double start, String description) {
		this.name = name;
		this.buyNow = bn;
		this.start = start;
		bid = 0;
		sold = false;
		topBid = "";

	}


	public String getName() {
		return name;
	}

	public double getBuyNow() {
		return buyNow;
	}


	public double getBid() {
		return bid;
	}

	public synchronized String newBid(double bid, String user) {
		if(user.equals(topBid)) {
			return "DOUBLE_BET";
		}
		if (bid < start) {
			return "UNDER_MIN";
		}
		if (bid == this.buyNow) {
			this.bid = bid;
			this.topBid = user;
			sold = true;
			return "SOLD";
		}
		else {
			this.bid = bid;
			topBid = user;
			return "BID";
		}
	}

	public boolean isSold() {
		return sold;
	}

	public void sold() {
		this.sold = false;
	}
	
	
	
}
