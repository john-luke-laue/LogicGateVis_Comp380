package com.sim;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.model.AndGate;
import com.model.Gate;
import com.model.InputGate;
import com.model.WeightedGraph;
import com.model.WeightedEdge;

//import org.newdawn.slick.AppGameContainer;
//import org.newdawn.slick.SlickException;




import demo.view.DemoBase;
import demo.view.DemoDefaults;
import y.base.Node;
import y.base.NodeCursor;
import y.layout.BufferedLayouter;
import y.layout.Layouter;
import y.layout.organic.SmartOrganicLayouter;
import y.layout.hierarchic.IncrementalHierarchicLayouter;
import y.layout.orthogonal.OrthogonalLayouter;
import y.layout.random.RandomLayouter;
import y.option.DoubleOptionItem;
import y.option.OptionHandler;
import y.view.Graph2D;
import y.view.Graph2DLayoutExecutor;
import y.view.NodeRealizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.JRootPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.swing.SwingWorker;




public class HardwareSim extends DemoBase implements ActionListener {

	/**
	 * @param args
	 */
	public static String currentInputFile;
	public static String currentExportFile;
	static ArrayList<Gate> gates = new ArrayList<Gate>();
	static ArrayList<WeightedEdge> edges = new ArrayList<WeightedEdge>(); 
	static int gid = 1; //gate id = the order in which the gate was created
	static ArrayList<String> outputGates = new ArrayList<String>(); 
	static ArrayList<ArrayList<Gate>> mainLeveledList= new ArrayList<ArrayList<Gate>>();
	static Node[] ygraphNodes = null;
	
	
	//ystuff..................................
	
	private JLabel statusLabel;
	private JProgressBar progressBar = new JProgressBar();
	private JComboBox layoutExecutionTypeBox;
	private JComboBox layouterBox;
	private static JTextField jtxtID;
	private static JTextField jtxtType;
	private static JTextField jtxtOuput;
	private static JTextField jtxtFlips;
	private static JTextField jtxtColor;
	private static JRadioButton jrbtnRand;
    private static JRadioButton jrbtnUser; 
	
	private TraverseGraph travClass;

	//end ystuff................................
	
	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		System.out.println("HardSim main start!");
	
		/*
		readFile();
		WeightedGraph<Gate> wgraph = new WeightedGraph<Gate>(gates, edges);
		wgraph.BellmanFord();
		wgraph.createLeveledList();
		mainLeveledList = wgraph.getLeveledList();
		*/
		
		//System.out.println();
		
		
		// SLICK! -------
		//AppGameContainer app = new AppGameContainer(new MainClass(wgraph.getVertices(), wgraph.getEdges(), wgraph.getLeveledList()));		  
		//app.setDisplayMode(800, 600, false); //width, height
		//app.start();
		
		
		// yGRAPH! ------
		EventQueue.invokeLater(new Runnable() {
		      public void run() {
		        Locale.setDefault(Locale.ENGLISH);
		        initLnF();
		        (new HardwareSim()).start();
		      }
		    });
		
		
		
		System.out.println("HardSim end main!");
	}
	
	public static void buildAbstractGraph() {
		readFile();
		WeightedGraph<Gate> wgraph = new WeightedGraph<Gate>(gates, edges);
		wgraph.BellmanFord();
		wgraph.createLeveledList();
		mainLeveledList = wgraph.getLeveledList();
		startBuild();
	}
	
	public static void readFile() {
		//before reading file, add dummy gate, gid = 0, id = G0
		Gate dummy = new Gate("G0", 0);
		dummy.setType("dummy");
		gates.add(dummy);
		
		try {            
			Scanner inputStream;
			if(currentInputFile == null) {
				inputStream = new Scanner(new FileInputStream("netlists\\simple.txt"));
			}
			else {
				inputStream = new Scanner( new FileInputStream(currentInputFile));
			}
			
			while(inputStream.hasNext()) {
				String id = "";
				String type = "";
				ArrayList<String> inputGates = null;
				
				String line = inputStream.nextLine();
				
				
				if(line.startsWith("INPUT")) {
					type = "input";
					Pattern p = Pattern.compile("\\((.*?)\\)",Pattern.DOTALL);
					Matcher matcher = p.matcher(line);
					if(matcher.find())
					{
					    id = matcher.group(1);
					}
				}
				else if(line.startsWith("OUTPUT")) {
					type = "output";
					Pattern p = Pattern.compile("\\((.*?)\\)",Pattern.DOTALL);
					Matcher matcher = p.matcher(line);
					if(matcher.find())
					{
					    id = matcher.group(1);
					    outputGates.add(id);
					}
				}
				else if(!line.isEmpty())
				{
					String[] tokens = line.split(" ");
					id = tokens[0];
					//tokens[2] = AND(G1,G3,...)
					if(tokens[2].startsWith("AND"))
						type = "and";
					else if(tokens[2].startsWith("OR"))
						type = "or";
					else if(tokens[2].startsWith("NOT"))
						type = "not";
					else if(tokens[2].startsWith("NAND"))
						type = "nand";
					else if(tokens[2].startsWith("NOR"))
						type = "nor";
					else if(tokens[2].startsWith("XOR"))
						type = "xor";
					else if(tokens[2].startsWith("XNOR"))
						type = "xnor";
					
					Pattern p = Pattern.compile("\\((.*?)\\)",Pattern.DOTALL);
					Matcher matcher = p.matcher(tokens[2]);
					if(matcher.find())
					{
						inputGates = new ArrayList<String>();
					    String[] inputGatesString = matcher.group(1).split(",");
					    for(int i = 0; i < inputGatesString.length; i++) {
					    	inputGates.add(inputGatesString[i]);
					    }
					    
					}
					
				}			
				
				if(type.equals("input")) {
					Gate temp = new Gate(id, gid);
					temp.setType("input");
					
					//take this out later...just defaulting the input to 1
					temp.addToInputList(1);
					//////////////////
					
					gates.add(temp);
					edges.add(new WeightedEdge(gates.get(0), temp, -1));
					gid++;
				}
				else if(type.equals("output")) {
					
				}
				else if(!type.equals("")) {
					Gate temp = new Gate(id, gid);
					temp.setType(type);
					gates.add(temp);
					gid++;
					
					Gate source = null;
					
					for(int a = 0; a < inputGates.size(); a++) {
						for(Gate g : gates) {
							if(g.getId().equals(inputGates.get(a)))
							{
								source = g;
								break;
							}
						}
						edges.add(new WeightedEdge(source, temp, -1));
						source.addConnectedGates(temp);
					}
				}
				else {
					//System.out.print("empty line in readfile()");
				}
			}
			inputStream.close();
		}
		catch (FileNotFoundException e)
        {
           System.out.println("File Not Found!");
           System.exit(0);
		}
	}
	//END READFILE() ..................................................................
	
	public static void exportFile() throws IOException {
		final File parentDir = new File(currentExportFile);
		final String hash = "HardwareSim-output";
		final String fileName = hash + ".txt";
		final File file = new File(parentDir, fileName);
		file.createNewFile(); 
		
		FileWriter fWriter = null;
		BufferedWriter writer = null; 
		try {
		  fWriter = new FileWriter(currentExportFile + "\\HardwareSim-output.txt");
		  writer = new BufferedWriter(fWriter);
		  
		  for(int i = 0; i < HardwareSim.mainLeveledList.size(); i++){
			  for(int j = 0; j < HardwareSim.mainLeveledList.get(i).size(); j++) {
				  String id = HardwareSim.mainLeveledList.get(i).get(j).getId();
				  String outputNum = Integer.toString(HardwareSim.mainLeveledList.get(i).get(j).getOutput());
				  writer.write(id + ":    " + outputNum);
				  writer.newLine();
			  }
		}
		   writer.close();
		   JOptionPane.showMessageDialog (null, "Export complete: " + currentExportFile + "\\HardwareSim-output.txt", 
				   "HardwareSim - Export", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
		}
	}
	
	
	
	//---------------------------------------------

	  

	  public HardwareSim() {
	    //build graph
		buildGraph( view.getGraph2D() );

	    view.setViewPoint2D(-200.0, -200.0);
	    
	    
	  }
	  
	  public static void startBuild() {
		  buildGraph( view.getGraph2D() );
	  }
	  

	  
	  private static class FlipPair {
	        private final String gateText; // gateText = Id
	        private final String typeText;
	        private final String outputText;
	        private final String flipsText;
	        private final String colorText;
	        
			FlipPair(String gateText, String typeText, String outputText, String flipsText, String colorText) {
	            this.gateText = gateText;
	            this.typeText = typeText;
	            this.outputText = outputText;
	            this.flipsText = flipsText;
	            this.colorText = colorText;
	        }
	    }
	  
	  public class TraverseGraph extends SwingWorker<Void, FlipPair> {
		  @Override
		 protected Void doInBackground() {
			  String gateText = "null";
			  String typeText = "null";
			  String outputText = "null";
			  String flipsText = "null";
			  String colorText = "purple";
		  NodePropertyHandler nodePropertyHandler = new NodePropertyHandler();
    	  nodePropertyHandler.updateValuesFromSelection(view.getGraph2D());
    	  final Graph2D graph = view.getGraph2D();
    	  
    	  for(int i = 0; i < HardwareSim.mainLeveledList.size(); i++){
			  for(int j = 0; j < HardwareSim.mainLeveledList.get(i).size(); j++) {
				  
				  String current = HardwareSim.mainLeveledList.get(i).get(j).getId();
				  gateText = current;
				  Gate temp = HardwareSim.mainLeveledList.get(i).get(j);
				  
				  if(temp.getType().equals("input")) {
					  String response = "";
					  if(HardwareSim.jrbtnUser.isSelected())
						  response = JOptionPane.showInputDialog(null, "Enter value (0 or 1): ", "Input Gate " + temp.getId(), JOptionPane.QUESTION_MESSAGE);
					  else {
						  Random r = new Random();
						  int rnum = r.nextInt(2); //between 0 and 1
						  response = Integer.toString(rnum);
					  }
					  
					  int output = Integer.parseInt(response);
					  temp.setInputGateOutput(output);
					  for(int z = 0; z < temp.getConnectedGates().size(); z++) {
						  temp.getConnectedGates().get(z).addToInputList(output);
					  }
					  
					  typeText = temp.getType();
					  
					  outputText = String.valueOf(output);
					  
					  temp.updateFlipCount();
					  flipsText = String.valueOf(temp.getFlipCount());
					  
					temp.updateColor();
					colorText = temp.getColor();
				  }
				  else if( !(temp.getType()).equals("dummy") ) {
					  temp.calcOutput();
					  int output = temp.getOutput();
					  
					  for(int z = 0; z < temp.getConnectedGates().size(); z++) {
						  temp.getConnectedGates().get(z).addToInputList(output);
					  }
					  
					  typeText = temp.getType();
					  
					  outputText = String.valueOf(output);
					  
					  temp.updateFlipCount();
					  flipsText = String.valueOf(temp.getFlipCount());
					  
					temp.updateColor();
					colorText = temp.getColor();
					  
				  }
				  else {
					  //System.out.println("Didn't catch TYPE of gate or its type GATE (dummy).");
				  }

				  publish(new FlipPair(gateText, typeText, outputText, flipsText, colorText));
				  
				  for(int k = 0; k < HardwareSim.ygraphNodes.length; k++) {
					  if(current.equals(graph.getRealizer(ygraphNodes[k]).getLabelText())) {
						  
						  nodePropertyHandler.commitNodeProperties(graph, ygraphNodes[k], temp);
				          
						  graph.updateViews();
				          
				          Layouter layouter = createLayouter();
				          applyLayoutAnimated(layouter);
				          
				          try {
							Thread.sleep(200);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				          
						  break;
					  }
				  }
				  
			  }
		    }
    	  
		  return null;
		  }
		 
		 @Override
	        protected void process(List<FlipPair> pairs) {
	            FlipPair pair = pairs.get(pairs.size() - 1);
	            if(pair.gateText != null);
	            	jtxtID.setText(pair.gateText);
	            	jtxtType.setText(pair.typeText);
	            	jtxtOuput.setText(pair.outputText);
	            	jtxtFlips.setText(pair.flipsText);
	            	jtxtColor.setText(pair.colorText);
	        }
		  
	  }

	  protected void configureDefaultRealizers() {
	    // painting shadows is expensive and not well suited for graphs with many
	    // nodes such as this demo's sample graph
	    DemoDefaults.registerDefaultNodeConfiguration(false);
	    DemoDefaults.configureDefaultRealizers(view);
	  }

	  /**
	   * Overwritten to add the status label and the progress bar.
	   */
	  public void addContentTo(JRootPane rootPane) {
	    this.statusLabel = new JLabel("Status");
	    final Dimension minimumSize = this.statusLabel.getMinimumSize();
	    this.statusLabel.setMinimumSize(new Dimension(Math.max(200, minimumSize.width), minimumSize.height));
	    final JPanel panel = new JPanel();
	    panel.add(this.statusLabel, BorderLayout.LINE_START);
	    this.progressBar.setMaximum(100);
	    this.progressBar.setMinimum(0);
	    this.progressBar.setValue(0);
	    
	    JPanel jpnlWest = new JPanel();
	    //GridLayout(int rows, int cols, int hgap, int vgap)
	    JPanel jpnlWestNorth = new JPanel();
	    
	    jpnlWest.setLayout(new BorderLayout());
	    jpnlWestNorth.setLayout(new GridLayout(0,2));
	    
	    
	    JButton jbtnPlay = new JButton("Play");
	    jbtnPlay.addActionListener(this);
	    
	    JButton jbtnStop = new JButton("Stop");
	    //!-> jbtnStop.addActionListener(this);
	    
	    JLabel jlblID = new JLabel("ID: ");
	    this.jtxtID = new JTextField("____");
	    
	    JLabel jlblType = new JLabel("Type: ");
	    this.jtxtType = new JTextField("____");
	    
	    JLabel jlblOuput = new JLabel("Output: ");
	    this.jtxtOuput = new JTextField("____");
	    
	    JLabel jlblFlips = new JLabel("Flips: ");
	    this.jtxtFlips = new JTextField("____");
	    
	    JLabel jlblColor = new JLabel("Color: ");
	    this.jtxtColor = new JTextField("____");
	    
	    this.jrbtnRand = new JRadioButton("Random", true);
	    this.jrbtnUser = new JRadioButton("User", false);
	    

	    ButtonGroup bgroup = new ButtonGroup();
	    bgroup.add(jrbtnRand);
	    bgroup.add(jrbtnUser);
	    
	    jpnlWestNorth.add(jbtnStop);
	    jpnlWestNorth.add(jbtnPlay);
	    jpnlWestNorth.add(jrbtnRand);
	    jpnlWestNorth.add(jrbtnUser);
	    jpnlWestNorth.add(jlblID);
	    jpnlWestNorth.add(jtxtID);
	    jpnlWestNorth.add(jlblType);
	    jpnlWestNorth.add(jtxtType);
	    jpnlWestNorth.add(jlblOuput);
	    jpnlWestNorth.add(jtxtOuput);
	    jpnlWestNorth.add(jlblFlips);
	    jpnlWestNorth.add(jtxtFlips);
	    jpnlWestNorth.add(jlblColor);
	    jpnlWestNorth.add(jtxtColor);
	    
	    jpnlWest.add(jpnlWestNorth, BorderLayout.NORTH);
	    getContentPane().add(jpnlWest, BorderLayout.WEST);
	    
	    panel.add(progressBar, BorderLayout.CENTER);
	    getContentPane().add(panel, BorderLayout.SOUTH);
	    super.addContentTo(rootPane);
	    
	  }

	  /** Creates graph nodes and edges. */
	  static void buildGraph(Graph2D graph) {
	    graph.clear();
	    Node[] nodes = new Node[HardwareSim.gates.size()];
	    
	    for(int i = 0; i < nodes.length; i++)
	    {
	      nodes[i] = graph.createNode();
	      graph.getRealizer(nodes[i]).setLabelText(HardwareSim.gates.get(i).getId());
	    }
	    
	    HardwareSim.ygraphNodes = nodes;
	    
	      for ( int j = 0; j < HardwareSim.edges.size(); j++ ) {
	        String srcID = HardwareSim.edges.get(j).getU().getId();
	        String dstID = HardwareSim.edges.get(j).getV().getId();
	        int nodeSrc = 0;
	        int nodeDst = 0;
	        for(int k = 0; k < nodes.length; k++) {
	        	if(srcID.equals(graph.getRealizer(nodes[k]).getLabelText())) {
	        		nodeSrc = k;
	        	}
	        	if(dstID.equals(graph.getRealizer(nodes[k]).getLabelText())) {
	        		nodeDst = k;
	        	}
	        }
	        graph.createEdge( nodes[nodeSrc], nodes[nodeDst] );
	      }
	    

	    (new BufferedLayouter(new RandomLayouter())).doLayout(graph);
	    
	    
	  }

	  /**
	   * Adds an extra layout action to the toolbar
	   */
	 
	  protected JToolBar createToolBar() {
		  final Action layoutAction = new AbstractAction(
		            "Layout", SHARED_LAYOUT_ICON) {
		      public void actionPerformed(ActionEvent e) {
		        applyLayout();
		      }
		    };
		    
		    
		    
	   
		  /*
		  final Action layoutAction = new AbstractAction("Layout", SHARED_LAYOUT_ICON) {
	      public void actionPerformed(ActionEvent e) {
	    	  NodePropertyHandler nodePropertyHandler = new NodePropertyHandler();
	    	  nodePropertyHandler.updateValuesFromSelection(view.getGraph2D());
	    	  final Graph2D graph = view.getGraph2D();
	    	  
	    	  for(int i = 0; i < HardwareSim.mainLeveledList.size(); i++){
				  for(int j = 0; j < HardwareSim.mainLeveledList.get(i).size(); j++) {
					  String current = HardwareSim.mainLeveledList.get(i).get(j).getId();
					  Gate temp = HardwareSim.mainLeveledList.get(i).get(j);
					  temp.setFlipCount(temp.getFlipCount() + 1);
					  System.out.print(temp.getFlipCount());	
					  
					  System.out.println("I'm in createToolBar()");
					  jtxtID.setText("AND");
					  System.out.print( "AND");

					  for(int k = 0; k < HardwareSim.ygraphNodes.length; k++) {
						  if(current.equals(graph.getRealizer(ygraphNodes[k]).getLabelText())) {
							  nodePropertyHandler.commitNodeProperties(graph, ygraphNodes[k]);
					          graph.updateViews();
					          applyLayout();
					          
					          try {
								Thread.sleep(300);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					          
							  break;
						  }
					  }
					  
				  }
			  }
	      }
	    };*/

	    // chooser for the layouter
	    layouterBox = new JComboBox(new Object[]{"Hierarchic"});
	    layouterBox.setMaximumSize(layouterBox.getPreferredSize());
	    layouterBox.setSelectedIndex(0);

	    // chooser for the execution type.
	    layoutExecutionTypeBox = new JComboBox(
	      new Object[]{"Animated"});
	    layoutExecutionTypeBox.setMaximumSize(layoutExecutionTypeBox.getPreferredSize());
	    layoutExecutionTypeBox.setSelectedIndex(0);

	    final JToolBar toolBar = super.createToolBar();
	    toolBar.addSeparator();
	    toolBar.add(createActionControl(layoutAction));
	    toolBar.addSeparator(TOOLBAR_SMALL_SEPARATOR);
	    toolBar.add(layouterBox);
	    toolBar.addSeparator(TOOLBAR_SMALL_SEPARATOR);
	    toolBar.add(layoutExecutionTypeBox);

	    return toolBar;
	  }

	  /**
	   * Configures and invokes a layout algorithm
	   */
	  void applyLayout() {
	    Layouter layouter = createLayouter();
	    switch (layoutExecutionTypeBox.getSelectedIndex()) {
	      case 0:
	    	  applyLayoutAnimated(layouter);
	        break;
	    }
	  }

	  /**
	   * Creates and returns a Layouter instance according to the given layout options.
	   */
	  Layouter createLayouter() {
	    switch (layouterBox.getSelectedIndex()) {
	      default:
	      case 0:
	        return new IncrementalHierarchicLayouter();
	    }
	  }

	  /**
	   * Applies the given layout algorithm to the graph
	   * This is done in a separate Thread asynchronously.
	   * Although the view and UI is responsive direct mouse and keyboard input is blocked.
	   * The layout process can be canceled and even killed through a dialog that is spawned.
	   */
	  void applyLayoutAnimatedThreaded(final Layouter layouter) {
	    this.progressBar.setIndeterminate(true);
	    final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.ANIMATED_THREADED);
	    // set a slow animation, so that the animation can easily be canceled.
	    layoutExecutor.getLayoutMorpher().setPreferredDuration(3000L);
	    layoutExecutor.getLayoutMorpher().setEasedExecution(true);
	    layoutExecutor.getLayoutMorpher().setSmoothViewTransform(true);
	    // lock the view so that the graph cannot be edited.
	    layoutExecutor.setLockingView(true);

	    final JDialog dialog = new JDialog(JOptionPane.getRootFrame(), "");

	    // the following method will return immediately and the layout and animation is performed in a new thread
	    // asynchronously.
	    final Graph2DLayoutExecutor.LayoutThreadHandle handle = layoutExecutor.doLayout(view, layouter, new Runnable() {
	      public void run() {
	        dialog.dispose();
	        progressBar.setIndeterminate(false);
	        statusLabel.setText("Layout Done");
	      }
	    }, new Graph2DLayoutExecutor.ExceptionListener() {
	      public void exceptionHappened(Throwable t) {
	        //dialog.dispose();
	        t.printStackTrace(System.err);
	        statusLabel.setText("Exception Happened.");
	      }
	    });

	    // this is visible because the layout is not blocking (this) EDT
	    this.statusLabel.setText("Layout is running");

	    final Box box = Box.createVerticalBox();
	    box.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	    final JLabel label = new JLabel("Layout Running [" + layouter.getClass().getName() + "].");
	    box.add(label);
	    box.add(Box.createVerticalStrut(12));
	    box.add(new JButton(new AbstractAction("Cancel") {
	      private boolean canceled;
	      public void actionPerformed(ActionEvent e) {
	        // first, simply cancel the layout
	        if (!canceled) {
	          handle.cancel();
	          statusLabel.setText("Cancelling");
	          label.setText("Canceled Thread.[" + layouter.getClass().getName() + "].");
	          ((JButton)e.getSource()).setText("Kill");
	          canceled = true;
	        } else {
	          // if it's not dead, yet, one could possibly try to kill the thread. 
	          // this is o.k. most of the time (no debugger, etc.), but should be used with care.
	          handle.getThread().stop();
	          setEnabled(false);
	          statusLabel.setText("Killed");
	        }
	      }
	    }));
	    dialog.getContentPane().add(box);
	    dialog.setLocationRelativeTo(view);
	    dialog.pack();

	    if (handle.isRunning()) {
	      dialog.setVisible(true);
	    }
	  }

	  /**
	   * Applies the given layout algorithm to the graph
	   * This is done synchronously blocking the calling Thread, thus leaving the view unresponsive during the layout.
	   */
	  void applyLayoutBuffered(final Layouter layouter){
	    final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.BUFFERED);
	    layoutExecutor.doLayout(view, layouter);
	  }

	  /**
	   * Applies the given layout algorithm to the graph in an animated fashion.
	   * This is done synchronously blocking the calling Thread, thus leaving the view unresponsive during the layout
	   * and animation.
	   */
	  void applyLayoutAnimated(final Layouter layouter){
	    // this won't be visible to the user because the EDT is blocked.
	    statusLabel.setText("Starting Animated Blocking Layout");
	    progressBar.setIndeterminate(true);
	    try {
	      final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.ANIMATED);
	      layoutExecutor.doLayout(view, layouter);
	    } finally {
	      progressBar.setIndeterminate(false);
	      statusLabel.setText("Animated Blocking Layout Done.");
	    }
	  }

	  /**
	   * Applies the given layout algorithm to the graph in an animated fashion using a blocking call
	   * from a separate newly spawned thread.
	   * This leaves the view responsive, but the view is still editable during the layout.
	   */
	  void applyLayoutAnimatedInOwnThread(final Layouter layouter){
	    statusLabel.setText("Starting own layout thread.");
	    progressBar.setIndeterminate(true);
	    final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.ANIMATED);
	    new Thread(new Runnable() {
	      public void run() {
	        try {
	          layoutExecutor.doLayout(view, layouter);
	        } finally {
	          SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	              statusLabel.setText("Layout Thread Finished.");
	              progressBar.setIndeterminate(false);
	            }
	          });
	        }
	      }
	    }).start();
	  }

	  /**
	   * Runs the layout in a separate thread, leaving the view responsive
	   * but the view is still editable during the layout.
	   * @param layouter
	   */
	  void applyLayoutThreaded(final Layouter layouter){
	    statusLabel.setText("Starting threaded layout");
	    progressBar.setIndeterminate(true);
	    final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.THREADED);
	    layoutExecutor.doLayout(view, layouter, new Runnable() {
	      public void run() {
	        statusLabel.setText("Layout Returned");
	        progressBar.setIndeterminate(false);
	      }
	    }, null);
	    statusLabel.setText("Return from doLayout()");
	  }

	  void applyLayoutUnbuffered(final Layouter layouter) {
	    final Graph2DLayoutExecutor layoutExecutor = new Graph2DLayoutExecutor(Graph2DLayoutExecutor.UNBUFFERED);
	    layoutExecutor.doLayout(view, layouter);
	  }

	  
	  
	  
	  //-----------------PROPERTIES
	  public static class NodePropertyHandler extends OptionHandler 
	  {
	    static final String ITEM_COLOR = "Color";

	    public NodePropertyHandler() {
	      super("Node Properties");
	      addColor(ITEM_COLOR, DemoDefaults.DEFAULT_NODE_COLOR, false, true, true, true).setValueUndefined(true);
	      // no work jtxtID.setText("AND");
	    }

	    /**
	     * Retrieves the values from the set of selected nodes (actually node 
	     * realizers) and stores them in the respective option items. 
	     */
	    public void updateValuesFromSelection(Graph2D graph)
	    {
	      set(ITEM_COLOR, Color.MAGENTA); 
	      //no work jtxtID.setText("AND");
	    }
	   
	    public void commitNodeProperties(Graph2D graph, Node n, Gate temp) 
	    {
	        NodeRealizer nr = graph.getRealizer(n); 
	        String gateColor = temp.getColor();
	        if(gateColor.equals("white")) {
	        	nr.setFillColor((Color)Color.MAGENTA);
	        }
	        else if(gateColor.equals("yellow")) {
	        	nr.setFillColor((Color)Color.YELLOW);
	        }
	        else {
	        	nr.setFillColor((Color)Color.RED);
	        }
	        
	        nr.repaint();
	        //no work jtxtID.setText("AND");
	    }
	  }




	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Hardware Sim - actionPerformed");
		
		//clear the input lists of the gates
		for(int i = 0; i < gates.size(); i++) {
			gates.get(i).clearInputList();
		}
		
		(travClass =  new TraverseGraph()).execute();
	}
	  
	
	
	
	
	
	

} //end all
