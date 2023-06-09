import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundBoard extends JFrame implements Runnable, AdjustmentListener, ActionListener {

    JToggleButton[][] button = new JToggleButton[37][180];
    JScrollPane buttonPane;
    JScrollBar tempoBar;
    JMenuBar menuBar;
    JMenu file, instrumentMenu, adjustColumns;
    JMenuItem save, load, addColumn, removeColumn, add20Columns, remove20Columns;
    JMenuItem[] instrumentItems;
    JButton stopPlay, clear, goBack;
    JFileChooser fileChooser;
    JLabel[] labels = new JLabel[button.length];
    JPanel buttonPanel, labelPanel, tempoPanel, menuButtonPanel;
    JLabel tempoLabel;
    boolean notStopped = true;
    JFrame frame = new JFrame();
    String[] clipNames;
    Clip[] clip;
    int tempo;
    boolean playing = false;
    int row = 0, col = 0;
    Font font = new Font("Times New Roman", Font.PLAIN, 10);
    String[] instrumentNames = { "Bell", "Glockenspiel", "Marimba", "Oboe", "oh_ah", "Piano" };

    public SoundBoard() {
        setSize(1000, 800);
        clipNames = new String[] { "C0", "B1", "ASharp1", "A1", "GSharp1", "G1", "FSharp1", "F1", "E1", "DSharp1", "D1",
                "CSharp1", "C1", "B2", "ASharp2", "A2", "GSharp2", "G2", "FSharp2", "F2", "E2", "DSharp2", "D2",
                "CSharp2", "C2", "B3", "ASharp3", "A3", "GSharp3", "G3", "FSharp3", "F3", "E3", "DSharp3", "D3",
                "CSharp3", "C3" };
        clip = new Clip[clipNames.length];
        String initInstrument = "\\" + instrumentNames[0] + "\\" + instrumentNames[0];
        try {
            for (int i = 0; i < clipNames.length; i++) {
                System.out.println(initInstrument + " - " + clipNames[i] + ".wav");
                URL url = this.getClass().getClassLoader().getResource(initInstrument + " - " + clipNames[i] + ".wav");
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);

                clip[i] = AudioSystem.getClip();
                clip[i].open(audioIn);
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(button.length, button[0].length, 2, 5));
        for (int r = 0; r < button.length; r++) {
            String name = clipNames[r].replaceAll("Sharp", "#");
            for (int c = 0; c < button[0].length; c++) {
                button[r][c] = new JToggleButton();
                button[r][c].setFont(font);
                button[r][c].setText(name);
                button[r][c].setPreferredSize(new Dimension(30, 30));
                button[r][c].setMargin(new Insets(0, 0, 0, 0));
                buttonPanel.add(button[r][c]);
            }
        }

        tempoBar = new JScrollBar(JScrollBar.HORIZONTAL, 200, 0, 50, 1000);
        tempoBar.addAdjustmentListener(this);
        tempo = tempoBar.getValue();
        tempoLabel = new JLabel("Tempo: " + tempo);
        tempoPanel = new JPanel(new BorderLayout());
        tempoPanel.add(tempoLabel, BorderLayout.WEST);
        tempoPanel.add(tempoBar, BorderLayout.CENTER);

        String currDir = System.getProperty("user.dir");
        fileChooser = new JFileChooser(currDir);

        menuBar = new JMenuBar();
        menuBar.setLayout(new GridLayout(1, 1));
        file = new JMenu("File");
        save = new JMenuItem("Save");
        load = new JMenuItem("Load");
        file.add(save);
        file.add(load);
        save.addActionListener(this);
        load.addActionListener(this);

        instrumentMenu = new JMenu("Instruments");
        instrumentItems = new JMenuItem[instrumentNames.length];
        for (int i = 0; i < instrumentNames.length; i++) {
            instrumentItems[i] = new JMenuItem(instrumentNames[i]);
            instrumentItems[i].addActionListener(this);
            instrumentMenu.add(instrumentItems[i]);
        }

        menuBar.add(file);
        menuBar.add(instrumentMenu);

        menuButtonPanel = new JPanel();
        menuButtonPanel.setLayout(new GridLayout(1, 3));
        stopPlay = new JButton("Play");
        stopPlay.addActionListener(this);
        menuButtonPanel.add(stopPlay);
        goBack = new JButton("Beginning");
        goBack.addActionListener(this);
        menuButtonPanel.add(goBack);
        clear = new JButton("Clear");
        clear.addActionListener(this);
        menuButtonPanel.add(clear);
        menuBar.add(menuButtonPanel, BorderLayout.EAST);

        adjustColumns = new JMenu("Adjust Columns");
        addColumn = new JMenuItem("Add Column");
        addColumn.addActionListener(this);
        removeColumn = new JMenuItem("Remove Column");
        removeColumn.addActionListener(this);
        add20Columns = new JMenuItem("Add 20 Columns");
        add20Columns.addActionListener(this);
        remove20Columns = new JMenuItem("Remove 20 Columns");
        remove20Columns.addActionListener(this);
        adjustColumns.add(remove20Columns);
        adjustColumns.add(removeColumn);
        adjustColumns.add(add20Columns);
        adjustColumns.add(addColumn);
		menuBar.add(adjustColumns);

        buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(buttonPane, BorderLayout.CENTER);
        this.add(tempoPanel, BorderLayout.SOUTH);
        this.add(menuBar, BorderLayout.NORTH);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread timing = new Thread(this);
        timing.start();
    }

    public void run() {
        do {
            try {
                if (!playing) {
                    new Thread();
                    Thread.sleep(0);
                } else {
                    for (int r = 0; r < button.length; r++) {
                        if (button[r][col].isSelected()) {
                            clip[r].start();
                            button[r][col].setForeground(Color.YELLOW);
                        }
                    }
                    new Thread();
                    Thread.sleep(tempo);
                    for (int r = 0; r < button.length; r++) {
                        if (button[r][col].isSelected()) {
                            clip[r].stop();
                            clip[r].setFramePosition(0);
                            button[r][col].setForeground(Color.BLACK);
                        }
                    }
                    col++;
                    if (col == button[0].length)
                        col = 0;
                }
            } catch (InterruptedException e) {
            }

        } while (true);
    }

    public static void main(String[] args) {
        new SoundBoard();
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        tempo = tempoBar.getValue();
        tempoLabel.setText("Tempo: " + tempo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stopPlay) {
            playing = !playing;
            if (!playing)
                stopPlay.setText("Play");
            else
                stopPlay.setText("Stop");
        }

        if (e.getSource() == goBack) {
            col = 0;
        }

        if (e.getSource() == load) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    File loadFile = fileChooser.getSelectedFile();
                    BufferedReader input = new BufferedReader(new FileReader(loadFile));
                    String temp;
                    temp = input.readLine();
                    tempo = Integer.parseInt(temp.substring(0, 2));
                    tempoBar.setValue(tempo);
                    Character[][] song = new Character[button.length][temp.length() - 2];

                    int r = 0;
                    System.out.println("Song: " + song.length + ", " + song[0].length);
                    while ((temp = input.readLine()) != null) {
                        for (int c = 0; c < song[0].length; c++) {
                            song[r][c] = temp.charAt(c);
                        }
                        r++;
                    }
                    setNotes(song);
                } catch (IOException E) {
                }
                col = 0;
                playing = false;
                notStopped = false;
                stopPlay.setText("Play");
            }
        }

        if (e.getSource() == save) {
            saveSong();
        }

        if (e.getSource() == clear) {
            for (int r = 0; r < button.length; r++) {
                for (int c = 0; c < button[0].length; c++) {
                    button[r][c].setSelected(false);
                }
            }
            tempoBar.setValue(200);
            playing = false;
            stopPlay.setText("Play");
            col = 0;
        }

        if (e.getSource() == addColumn) {
            resizeButtons(1);
            playing = false;
            stopPlay.setText("Play");
        }

        if (e.getSource() == add20Columns) {
            resizeButtons(20);
            playing = false;
            stopPlay.setText("Play");
        }

        if (e.getSource() == removeColumn) {
            if (button[0].length - 1 > 1) {
                resizeButtons(-1);
                playing = false;
                col = button[0].length - 1;
                stopPlay.setText("Play");
            }
        }

        if (e.getSource() == remove20Columns) {
            if (button[0].length - 1 > 1) {
                resizeButtons(-20);
                playing = false;
                col = button[0].length - 1;
                stopPlay.setText("Play");
            }
        }

        for (int i = 0; i < instrumentItems.length; i++) {
            if (e.getSource() == instrumentItems[i]) {
                String selectedInstrument = "\\" + instrumentNames[i] + "\\" + instrumentNames[i];
                try {
                    for (int j = 0; j < clipNames.length; j++) {
                        URL url = this.getClass().getClassLoader().getResource(selectedInstrument + " - " + clipNames[j] + ".wav");
                        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                        clip[j] = AudioSystem.getClip();
                        clip[j].open(audioIn);
                    }
                } catch (UnsupportedAudioFileException ee) {
                    ee.printStackTrace();
                } catch (IOException ee) {
                    ee.printStackTrace();
                } catch (LineUnavailableException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    public void setNotes(Character[][] notes) {
        buttonPane.remove(buttonPanel);
        buttonPanel = new JPanel();
        button = new JToggleButton[37][notes[0].length];
        buttonPanel.setLayout(new GridLayout(button.length, button[0].length, 2, 5));
        for (int r = 0; r < button.length; r++) {
            String name = clipNames[r].replaceAll("Sharp", "#");
            for (int c = 0; c < button[0].length; c++) {
                button[r][c] = new JToggleButton();
                button[r][c].setFont(font);
                button[r][c].setText(name);
                button[r][c].setPreferredSize(new Dimension(30, 30));
                button[r][c].setMargin(new Insets(0, 0, 0, 0));
                buttonPanel.add(button[r][c]);
            }
        }
        this.remove(buttonPane);
        buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(buttonPane, BorderLayout.CENTER);

        for (int r = 0; r < button.length; r++) {
            for (int c = 0; c < button[0].length; c++) {
                try {
                    if (notes[r][c] == 'x')
                        button[r][c].setSelected(true);
                    else
                        button[r][c].setSelected(false);
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
                    System.out.println(e);
                }
            }
        }

        this.revalidate();
    }

    public void saveSong() {
        FileFilter filter = new FileNameExtensionFilter("*.txt", "txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String st = file.getAbsolutePath();
                if (st.indexOf(".txt") >= 0)
                    st = st.substring(0, st.length() - 4);
                String output = "";

                for (int r = 0; r < button.length + 1; r++) {
                    if (r == 0) {
                        output += tempo;
                        for (int i = 0; i < button[0].length; i++)
                            output += " ";
                    } else {
                        for (int c = 0; c < button[0].length; c++) {
                            if (button[r - 1][c].isSelected())
                                output += ("x");
                            else
                                output += ("-");
                        }
                    }
                    output += ("\n");
                }

                BufferedWriter outputStream = new BufferedWriter(new FileWriter(st + ".txt"));
                outputStream.write(output);
                outputStream.close();

            } catch (IOException io) {
            }
        }
    }

    public void resizeButtons(int change){
        JToggleButton [] [] temp = new JToggleButton[button.length][button[0].length+change];
        for(int r =0; r<temp.length;r++){
            for(int c =0; c<temp[0].length;c++ ){
                temp [r][c] = new JToggleButton();
                try{
                    if(button[r][c].isSelected()){
                        temp[r][c].setSelected(true);
					}
					else{
						temp[r][c].setSelected(false);
                    }
                }catch(ArrayIndexOutOfBoundsException e){

                }
            }
        }
        buttonPane.remove(buttonPanel);
        buttonPanel=new JPanel();
        button = new JToggleButton[temp.length][temp[0].length];
        buttonPanel.setLayout(new GridLayout(button.length,button[0].length));
        for (int r = 0; r < temp.length; r++) {
            String name = clipNames[r].replaceAll("Sharp", "#");
            for (int c = 0; c < button[0].length; c++) {
                button[r][c] = new JToggleButton();
                button[r][c].setFont(font);
                button[r][c].setText(name);
                button[r][c].setPreferredSize(new Dimension(30, 30));
                button[r][c].setMargin(new Insets(0, 0, 0, 0));
                buttonPanel.add(button[r][c]);
            }
        }
        this.remove(buttonPane);
        buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(buttonPane, BorderLayout.CENTER);

        for (int r = 0; r < temp.length; r++) {
            for (int c = 0; c < temp[0].length; c++) {
                try {
                    if (temp[r][c].isSelected())
                        button[r][c].setSelected(true);
                    else
                        button[r][c].setSelected(false);
                } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {

                }
            }
        }

        this.revalidate();

    }

}