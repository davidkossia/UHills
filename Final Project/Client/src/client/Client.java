/* CHATROOM <ClientMain.java>
 * EE422C Project 7 submission by
 * Replace <...> with your actual data.
 * Jake Morrissey
 * jmm9683
 * 16345
 * Slip days used: 0
 * Fall 2018
 */
package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.ListIterator;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;


public class Client extends Application {

	private String name;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String delim = Character.toString((char) 31);
	private String userDelim = Character.toString((char) 29);
	private Stage window = null;
	private HashMap<Integer, ChatBox> chatBoxs = new HashMap<Integer, ChatBox>();
	private VBox box;
	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	@Override
	public void start(Stage startStage) throws Exception {

		window = startStage;
		box = new VBox();
		box.setPadding(new Insets(50, 50, 50, 50));

		Label title = new Label("Welcome to eHills Auction");
		title.setTextFill(Color.DARKORANGE);
		title.setFont(Font.font(24));
		Label ipTitle = new Label("Enter IP Address: (ex: \"192.0.0.1\")");
		ipTitle.setFont(Font.font(12));
		TextField ip = new TextField();
		ip.setPromptText("192.0.0.1");
		ip.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER)  {
					if(!ip.getText().equals("")) {
						try {
							connectScreen(ip.getText());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		Button ipValue = new Button("Connect");
		ipValue.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if(!ip.getText().equals("")) {
					try {
						connectScreen(ip.getText());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		box.getChildren().addAll(title, ipTitle, ip, ipValue);
		Scene scene = new Scene(box, 500, 500);
		window.setScene(scene);
		window.show();
	}
	private void connectScreen(String ip) throws Exception {

		this.socket = new Socket(ip, 4001);
		InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
		reader = new BufferedReader(streamReader);
		
		try
		{
			writer = new PrintWriter(socket.getOutputStream());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Thread userThreads = new Thread(new newUser()); 
		userThreads.start();
		loginScreen();
	}

	public void loginScreen() {
		box.getChildren().clear();
		box = new VBox();
		box.setPadding(new Insets(50, 50, 50, 50));
		box.setSpacing(10);

		Label usernameLabel = new Label("Username:");
		TextField username = new TextField();

		Label pswdLabel = new Label("Password:");
		TextField pswd = new TextField();

		Button guest = new Button("Login as guest");
		guest.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				name = "Guest";
				writer.println("CREATE" + delim + name + delim + "");
				writer.flush();
				writer.println("ACTIVATE" + delim + name);
				writer.flush();
			
			}

		});
		
		Button create = new Button("Create Account");
		create.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String thisUsername = username.getText();
				String thisPW = pswd.getText();

				if (!thisUsername.equals("") && !thisPW.equals("")) {
					name = thisUsername;
					writer.println("CREATE" + delim + thisUsername + delim + thisPW);
					writer.flush();
					writer.println("ACTIVATE" + delim + name);
					writer.flush();
				}
				
				else if (!thisUsername.equals("") && thisPW.equals("")) {
					name = thisUsername;
					writer.println("CREATE" + delim + thisUsername + delim + thisPW);
					writer.flush();
					writer.println("ACTIVATE" + delim + name);
					writer.flush();
				}
			}

		});

		Button login = new Button("Login");
		login.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String thisUsername = username.getText();
				String thisPswd = pswd.getText();

				if (!thisUsername.equals("") && !thisPswd.equals("")) {
					name = thisUsername;
					writer.println("LOGIN" + delim + thisUsername + delim + thisPswd);
					writer.flush();
					writer.println("ACTIVATE" + delim + name);
					writer.flush();

				}	
			}

		});
		pswd.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER)  {
					String thisUsername = username.getText();
					String thisPswd = pswd.getText();
					if (!thisUsername.equals("") && !thisPswd.equals("")) {
						name = thisUsername;
						writer.println("LOGIN" + delim + thisUsername + delim + thisPswd);
						writer.flush();
						writer.println("ACTIVATE" + delim + name);
						writer.flush();

					}	
				}
			}
		});

		box.getChildren().addAll(usernameLabel,  username , pswdLabel, pswd, login, guest, create);
		Scene scene = new Scene(box, 400, 400);
		window.setScene(scene);
		window.show();
	}

	public void notUnique(String[] message) { 
		String error = "ERROR: \"" + name + "\" already exists.";
		Label alert = new Label();
		alert.setText(error);
		alert.setWrapText(true);
		box.getChildren().add(alert);
	}
	public void alreadyActive(String[] message) {

		for(ListIterator<Node> iterator = box.getChildren().listIterator(); iterator.hasNext();) {
			Node it = iterator.next();
			if (it instanceof Label && ((Label)it).getText().substring(0,6).equals("ERROR:")) {
				iterator.remove();
			}
		}

		String error = "ERROR: Already logged in.";
		Label alert = new Label();
		alert.setText(error);
		alert.setWrapText(true);
		box.getChildren().add(alert);

	}

	public void wrongPswd(String[] message) { 

		for(ListIterator<Node> iterator = box.getChildren().listIterator(); iterator.hasNext();) {
			Node it = iterator.next();
			if (it instanceof Label && ((Label)it).getText().substring(0,6).equals("ERROR:")) {
				iterator.remove();
			}
		}

		String error = "ERROR: Incorrect Password.";
		Label alert = new Label();
		alert.setText(error);
		alert.setWrapText(true);
		box.getChildren().add(alert);
	}

	public void loginScreen(String[] activeUsers) {
		window.close();
		window = new Stage();
		window.setTitle("User:" + name);
		TabPane tabs = new TabPane();
		
		VBox pane = new VBox();
		Tab auction = new Tab("Auction");
		auction.setClosable(false);
		Text itemSelect = new Text("Select an Item");
		ChoiceBox<String> items = new ChoiceBox<>();
		ObservableList<String> itemsList = items.getItems();
		//TODO: add item names to list
		items.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number n1, Number n2) {
				//change screen elements
				String currItem = items.getItems().get((Integer)n2);
				writer.println("SELECT" + delim + currItem);
				writer.flush();
			}
		});
		
		Text itemName = new Text("Item:");
		Text in = new Text();
		Text currBid = new Text("Current Bid Information");
		Text cb = new Text();
		Text description = new Text("Description");
		Text desc = new Text();
		Label bidLabel = new Label();
		TextField bid = new TextField();
		Button placeBid = new Button("Bid");
		placeBid.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				Double bidAmount = Double.parseDouble(bid.getText());
				//check bid
				//update bid info on item
				writer.println("BID"+ delim + bidAmount);
				bid.setText("");
				bid.requestFocus();
			
			}

		});
		
		Button logOut1 = new Button("Logout");
		logOut1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				writer.println("LOGOUT" + delim + name);
				writer.flush();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				writer.println("ACTIVATE" + delim + name);
				writer.flush();

				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				window.close();
				System.exit(0);
			}
		});
		
		pane.getChildren().addAll(itemSelect, items, itemName, in, currBid, cb, description, desc, bidLabel, bid, placeBid, logOut1);
		auction.setContent(pane);
		
		//bid history tab
		Tab history = new Tab("Bid History");
		history.setClosable(false);
		TextArea bids = new TextArea();
		bids.resize(200, 400);
		bids.setEditable(false);
		Button logOut2 = new Button("Logout");
		logOut2.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				writer.println("LOGOUT" + delim + name);
				writer.flush();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				writer.println("ACTIVATE" + delim + name);
				writer.flush();

				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				window.close();
				System.exit(0);
			}
		});
		VBox pane2 = new VBox();
		//auto scroll
		bids.textProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed (ObservableValue<?> obs, Object ov, Object nv) {
				bids.setScrollTop(Double.MAX_VALUE);
			}
		});
		
		pane2.getChildren().addAll(bids, logOut2);
		history.setContent(pane2);
		
		tabs.getTabs().add(auction);
		tabs.getTabs().add(history);
		tabs.setPadding(new Insets(10, 10, 10, 10));
		
		

		
		
		window.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				
				for (ChatBox chats : chatBoxs.values()) {
					chats.chatStage.close();
				}
				writer.println("LOGOUT" + delim + name);
				writer.flush();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				writer.println("ACTIVATE" + delim + name);
				writer.flush();

				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				window.close();
				System.exit(0);
			}


		});

		Scene scene = new Scene(tabs, 500, 500);
		window.setScene(scene);
		window.show();
	}


	public void startChat(String[] message) {
		int ID = Integer.parseInt(message[0]);
		ChatBox newChat = new ChatBox(ID);
		if(chatBoxs.containsKey(ID)) {
			newChat.setTitle(chatBoxs.get(ID).getTitle());
			chatBoxs.get(ID).close();
		}
		newChat.updateChat(message);
		chatBoxs.put(ID, newChat);
	}


	class ChatBox extends Application {

		private int ID;
		private Stage chatStage;
		private ScrollPane scrollPane;
		private GridPane gridPane;
		private int row = 0;

		public ChatBox(int id) {

			ID = id;
			this.chatStage = new Stage();
			gridPane = new GridPane();
			scrollPane = new ScrollPane();

			TextField msg = new TextField();
			chatStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					
					writer.println("CLOSECHAT" + delim + ID);
					writer.flush();
				}


			});

			Button send = new Button("SEND");
			send.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					String s = msg.getText();
					if (s != null) {
						sendMessage(s);
						msg.clear();
					}
				}

			});


			msg.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent keyEvent) {
					if (keyEvent.getCode() == KeyCode.ENTER)  {
						String s = msg.getText();
						if (s != null) {
							sendMessage(s);
							msg.clear();
						}
					}
				}
			});
			

			VBox box = new VBox();
			GridPane screen = new GridPane();
			screen.getRowConstraints().add(new RowConstraints(250));
			screen.getRowConstraints().add(new RowConstraints(50));
			screen.getColumnConstraints().add(new ColumnConstraints(325));
			screen.getColumnConstraints().add(new ColumnConstraints(75));
			screen.add(scrollPane, 0, 0);
			screen.add(msg,0,1);
			screen.add(send, 1, 1);

			scrollPane.setContent(gridPane);
			box.getChildren().addAll(screen);
			Scene scene = new Scene(box, 400, 300);
			chatStage.setScene(scene);
			chatStage.show();
		}
		
		public void close() {
			chatStage.close();
		}

		
		public void setTitle(String title) {
			chatStage.setTitle(title);
		}
		
		public String getTitle() {
			return chatStage.getTitle();
		}

		@Override
		public void start(Stage arg0) throws Exception {}
		
		public int getID() {
			return ID;
		}

		public void updateChat(String[] message) {
			

			TextFlow text = new TextFlow();
			Text user = new Text(message[1] + ": " );
			Text msg = new Text(message[2]);
			text.getChildren().addAll(user,msg);
			if (message[1].equals("SERVER")) {
				user.setFill(Color.RED);
				msg.setFill(Color.RED);
			}
			else if (!message[1].equals(name)) {
				user.setFill(Color.DARKORANGE);
				msg.setFill(Color.DARKORANGE);
			} 

			text.setMaxWidth(scrollPane.getWidth());
			text.setBorder(null);
			gridPane.add(text, 1, row);
			row++;
			
			scrollPane.setVvalue(1.0);
		}

		public void sendMessage(String message) {
			writer.println(ID + delim + name + delim + message);
			writer.flush();
		}
	}

	
	class newUser implements Runnable {

		@Override
		public void run() {
			
			String user;
			
			try {
				while ((user = reader.readLine()) != null) {
					

					String[] action = user.split(delim);

		
					if (action[0].equals("ACTIVATE")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								loginScreen(action[2].split(userDelim));
							}
						});
					}
					else if(action[0].equals("ALREADYACTIVE")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								alreadyActive(action);
							}
						});
					}
					else if (action[0].equals("WRONGPSWD")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								wrongPswd(action);
							}
						});
					}
					else if (isNumeric(action[0])) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								int ID = Integer.parseInt(action[0]);
								if (chatBoxs.containsKey(ID)) {
									if(action[1].equals("SERVER") && action[2].equals("New chat created."))
										startChat(action);
									else
										chatBoxs.get(ID).updateChat(action);
								}
								else {
									startChat(action);
								}	
							}
						});
					}
					else if (action[0].equals("NOTUNIQUE")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() { 
								notUnique(action);
							}
						});
					}
				}
			} catch (IOException ex) { 
				if(ex instanceof SocketException) {}
				else ex.printStackTrace(); 	}		
		}
	}
	private boolean isNumeric(String str) {  
		try {  
			Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe) {  
			return false;  
		}  
		return true;  
	}

}