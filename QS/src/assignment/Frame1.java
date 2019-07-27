package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class Frame1 extends javax.swing.JFrame {

     /**
     * Generate ticket number for category A,B,C based on number ranges. Store
     * the numbers in the queue for each categories.
     */
    static List<String> qA = new ArrayList(); //queue for category A
    static List<String> qB = new ArrayList(); //queue for category B
    static List<String> qC = new ArrayList(); //queue for category C
    static int[] butrep = {0, 0, 0, 0}; //array to control number of repeat button pressed
    static int numa = 100; //number range category A
    static int numb = 200; //number range category B
    static int numc = 300; //number range category C
    static int maxQue = 40; //maximum queue number
    static int curQue = 0; //current queue number
    static StringBuilder logText = new StringBuilder(); //for logging
    static DefaultListModel<String> dispQ = new DefaultListModel(); //to display calling numbers

    /**
     * Creates new form Frame1
     */
    public Frame1() {
        initComponents();  // connection between the GUI Editor and Java
    }
    //to get the tickets
    public static String dispenseTicket(String category) {
        //if queue number is more than 40
        if (curQue > maxQue) {
            return "";  
        }
        curQue++; //increase queue number
        String numstr = "";
        switch (category) {
            case "A":
                numa++; //current number for A
                numstr = String.format("%03d", numa);
                qA.add(numstr);
                break;
            case "B":
                numb++; //current number for B
                numstr = String.format("%03d", numb);
                qB.add(numstr);
                break;
            case "C":
                numc++; //current number for C
                numstr = String.format("%03d", numc);
                qC.add(numstr);
                break;
        }
        logger("Ticket " + numstr + " created for category " + category);
       

        return numstr;
    }

    /*
    *Pass the queue number to the counters.
    *Play audio of the displayed numbers
     */
    public static String callTicket(String counter) {
        curQue--; //decrease queue numbers called
        String numstr = "";
        switch (counter) {
            case "1":
            case "2":
                numstr = qA.get(0);
                qA.remove(0);
                break;
            case "3":
                numstr = qB.get(0);
                qB.remove(0);
                break;
            case "4":
                numstr = qC.get(0);
                qC.remove(0);
                break;
        }
        dispQ.addElement(" " + numstr + "          " + counter); //display queue number with their counter
        //to make sure there's only 4 queue number on display
        if (dispQ.size() > 4) {
            dispQ.remove(0);
        }
        delaySpeak(numstr, counter);
        logger("Ticket " + numstr + " called at counter " + counter);
        return numstr;
    }

    /*
    *To repeat the queue numbers.
     Play the audio of the repeated numbers.
     */
    public static boolean repeatTicket(String numstr, String counter) {
        logger("Ticket " + numstr + " repeated at counter " + counter);
        for (int i = 0; i < dispQ.size(); i++) {
            if (dispQ.get(i).substring(1, 4).equals(numstr)) { // 1 is start index (inclusive) and 4 is end index (exclusive)
                dispQ.remove(i); //remove any previous number on the screen that is the repeated numbers 
                break;
            }
        }
        dispQ.addElement(" " + numstr + "          " + counter);
       //to display the repeated number at the bottom
        if (dispQ.size() > 4) {
            dispQ.remove(0);
        }
        delaySpeak(numstr, counter); //play the audio of the repeated numbers and counter
        
        int j = Integer.parseInt(counter) - 1;
        butrep[j]++;
        //to make sure numbers can only be repeated three times
        if (butrep[j] >= 3) {
            butrep[j] = 0;
            return false;
        } else {
            return true;
        }
    }

    //for logging
    public static void logger(String actionlog) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); //timestamp
        logText.append(dateFormat.format(new Date()));
        logText.append(" : ");
        logText.append(actionlog);
        logText.append("\n");
    }

    //Set the timer of the audio
    public static void delaySpeak(String numstr, String counter) {
        final String finNumstr = numstr;
        Timer timer = new Timer(100, new ActionListener() { //timer for action, make sure number is said first
            public void actionPerformed(ActionEvent e) {
                speakMsg(finNumstr, counter);
            }
        });
        timer.setRepeats(false); //make sure only once 
        timer.start(); //to start the timer
    }

    //Method to play the audio of the numbers
    private static void speakMsg(String numstr, String counter) {
        try {
            soundwav("Calling-Tone4.wav");
            char[] ch = numstr.toCharArray();
            for (char a : ch) {
                try {
                    String filename = "Speak-" + String.valueOf(a) + ".wav"; 
                    soundwav(filename);
                    Thread.sleep(600);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Frame1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Thread.sleep(300);
            soundwav("counter.wav");
            Thread.sleep(800);
            String filename = "Speak-" + counter + ".wav";
            soundwav(filename);
            Thread.sleep(700);
        } catch (InterruptedException ex) {
            Logger.getLogger(Frame1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void soundwav(String fname) {
        try {
            FileInputStream fis = new FileInputStream(fname);
            AudioStream AuStream = new AudioStream(fis);
            AudioData AuData = AuStream.getData();
            AudioDataStream ADS = null;
            ContinuousAudioDataStream loop = null;
            ADS = new AudioDataStream(AuData);
            AudioPlayer.player.start(ADS);
            fis.close(); //close file
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Frame1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Frame1.class.getName()).log(Level.SEVERE, null, ex);
            }    
    }
//to make sure queue number must be below 40
    private void CheckMaxQue() {
        String line = "Total Queue = " + String.valueOf(curQue); //current queue number
        if (curQue == 0) {
            line = "No More Queue To Be Processed";
            logger(line);          //Log into log status

        }
        if (curQue == maxQue) {
            line = "Maximum Queue Reached";
            logger(line);          //Log into log status
        }
        jLabel1.setText(line); //Display on Status bar
        if (curQue >= maxQue) {
            //button is disabled if the queue numbers exceed 40
            catA.setEnabled(false);
            catB.setEnabled(false);
            catC.setEnabled(false);
        } else {
            //button is enabled if the queue numbers exceed 40
            catA.setEnabled(true);
            catB.setEnabled(true);
            catC.setEnabled(true);
        }
        if (qA.isEmpty()) {
            next1.setEnabled(false);
            next2.setEnabled(false);
        } else {
            next1.setEnabled(true);
            next2.setEnabled(true);
        }   
        if (qB.isEmpty()) next3.setEnabled(false);
        else next3.setEnabled(true);
        if (qC.isEmpty()) next4.setEnabled(false);
        else next4.setEnabled(true);
        if (!dispQ.isEmpty())
            jList1.setModel(dispQ); //To display the calling numbers on screen
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabelC1 = new javax.swing.JLabel();
        next1 = new javax.swing.JButton();
        jButtonR1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabelC2 = new javax.swing.JLabel();
        next2 = new javax.swing.JButton();
        jButtonR2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabelC3 = new javax.swing.JLabel();
        next3 = new javax.swing.JButton();
        jButtonR3 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabelC4 = new javax.swing.JLabel();
        next4 = new javax.swing.JButton();
        jButtonR4 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        exit = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        front1 = new javax.swing.JTextField();
        mid1 = new javax.swing.JTextField();
        last1 = new javax.swing.JTextField();
        front2 = new javax.swing.JTextField();
        mid2 = new javax.swing.JTextField();
        last2 = new javax.swing.JTextField();
        front3 = new javax.swing.JTextField();
        mid3 = new javax.swing.JTextField();
        last3 = new javax.swing.JTextField();
        catA = new javax.swing.JButton();
        catC = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        catB = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();

        jButton2.setText("Exit");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel11.setText("Log");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(23, 23, 23))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 102, 153));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Counter 1");

        jLabelC1.setBackground(new java.awt.Color(255, 204, 204));
        jLabelC1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabelC1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelC1.setOpaque(true);

        next1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        next1.setText("Next number");
        next1.setEnabled(false);
        next1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next1ActionPerformed(evt);
            }
        });

        jButtonR1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonR1.setText("Repeat number");
        jButtonR1.setEnabled(false);
        jButtonR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonR1ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Counter 2");

        jLabelC2.setBackground(new java.awt.Color(255, 204, 204));
        jLabelC2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabelC2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelC2.setOpaque(true);

        next2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        next2.setText("Next number");
        next2.setEnabled(false);
        next2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next2ActionPerformed(evt);
            }
        });

        jButtonR2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonR2.setText("Repeat number");
        jButtonR2.setEnabled(false);
        jButtonR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonR2ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Counter 3");

        jLabelC3.setBackground(new java.awt.Color(255, 204, 204));
        jLabelC3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabelC3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelC3.setOpaque(true);

        next3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        next3.setText("Next number");
        next3.setEnabled(false);
        next3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next3ActionPerformed(evt);
            }
        });

        jButtonR3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonR3.setText("Repeat number");
        jButtonR3.setEnabled(false);
        jButtonR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonR3ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Counter 4");

        jLabelC4.setBackground(new java.awt.Color(255, 204, 204));
        jLabelC4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabelC4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelC4.setOpaque(true);

        next4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        next4.setText("Next number");
        next4.setEnabled(false);
        next4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                next4ActionPerformed(evt);
            }
        });

        jButtonR4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButtonR4.setText("Repeat number");
        jButtonR4.setEnabled(false);
        jButtonR4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonR4ActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(250, 240, 240));
        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Counters");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(next2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonR2))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(next3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonR3))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(next4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonR4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(17, 17, 17))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addGap(14, 14, 14)))))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGap(19, 19, 19)
                                        .addComponent(jLabelC4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelC2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelC3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(13, 13, 13))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(next1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonR1))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelC1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabelC1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(next1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonR1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabelC2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(next2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonR2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelC3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(next3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonR3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabelC4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(next4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonR4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );

        exit.setText("Exit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(255, 102, 153));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Technical Assistance");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("Billing/Payment");

        front1.setBackground(new java.awt.Color(255, 204, 204));
        front1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        front1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        front1.setMaximumSize(new java.awt.Dimension(50, 50));
        front1.setMinimumSize(new java.awt.Dimension(50, 50));
        front1.setPreferredSize(new java.awt.Dimension(50, 50));

        mid1.setBackground(new java.awt.Color(255, 204, 204));
        mid1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        mid1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mid1.setMaximumSize(new java.awt.Dimension(50, 50));
        mid1.setMinimumSize(new java.awt.Dimension(50, 50));
        mid1.setPreferredSize(new java.awt.Dimension(50, 50));

        last1.setBackground(new java.awt.Color(255, 204, 204));
        last1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        last1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        last1.setAlignmentX(0.0F);
        last1.setMaximumSize(new java.awt.Dimension(50, 50));
        last1.setMinimumSize(new java.awt.Dimension(50, 50));
        last1.setPreferredSize(new java.awt.Dimension(50, 50));

        front2.setBackground(new java.awt.Color(255, 204, 204));
        front2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        front2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        front2.setMaximumSize(new java.awt.Dimension(50, 50));
        front2.setMinimumSize(new java.awt.Dimension(50, 50));
        front2.setPreferredSize(new java.awt.Dimension(50, 50));

        mid2.setBackground(new java.awt.Color(255, 204, 204));
        mid2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        mid2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mid2.setMaximumSize(new java.awt.Dimension(50, 50));
        mid2.setMinimumSize(new java.awt.Dimension(50, 50));
        mid2.setPreferredSize(new java.awt.Dimension(50, 50));

        last2.setBackground(new java.awt.Color(255, 204, 204));
        last2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        last2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        last2.setAlignmentX(0.0F);
        last2.setMaximumSize(new java.awt.Dimension(50, 50));
        last2.setMinimumSize(new java.awt.Dimension(50, 50));
        last2.setPreferredSize(new java.awt.Dimension(50, 50));

        front3.setBackground(new java.awt.Color(255, 204, 204));
        front3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        front3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        front3.setMaximumSize(new java.awt.Dimension(50, 50));
        front3.setMinimumSize(new java.awt.Dimension(50, 50));
        front3.setPreferredSize(new java.awt.Dimension(50, 50));

        mid3.setBackground(new java.awt.Color(255, 204, 204));
        mid3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        mid3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mid3.setMaximumSize(new java.awt.Dimension(50, 50));
        mid3.setMinimumSize(new java.awt.Dimension(50, 50));
        mid3.setPreferredSize(new java.awt.Dimension(50, 50));

        last3.setBackground(new java.awt.Color(255, 204, 204));
        last3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        last3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        last3.setAlignmentX(0.0F);
        last3.setMaximumSize(new java.awt.Dimension(50, 50));
        last3.setMinimumSize(new java.awt.Dimension(50, 50));
        last3.setPreferredSize(new java.awt.Dimension(50, 50));

        catA.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        catA.setText("Category A");
        catA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catAActionPerformed(evt);
            }
        });

        catC.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        catC.setText("Category C");
        catC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catCActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("General Enquiry");

        catB.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        catB.setText("Category B");
        catB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catBActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Ticket Dispenser");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(front2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mid2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(last2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(front3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mid3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(last3, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(front1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mid1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(last1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(catB, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(45, 45, 45))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(60, 60, 60))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(catA, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(catC, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mid1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(last1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(front1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(catA, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mid2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(last2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(front2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(catB, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mid3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(last3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(front3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(catC, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Queue Number");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setText("Service Counter");

        jList1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jList1.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(21, 21, 21))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jButton1.setText("Log");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(0, 204, 204));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);

        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jTextArea3.setRows(5);
        jTextArea3.setText("\n              HOSPITAL WANITA \n                          DAN \n                  KANAK-KANAK");
        jScrollPane4.setViewportView(jTextArea3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(exit)
                            .addComponent(jButton1))
                        .addGap(37, 37, 37)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(61, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
    //Exit button
        System.exit(0);
    }//GEN-LAST:event_exitActionPerformed

    private void catAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catAActionPerformed
    //Button for category A    
        String numstr = dispenseTicket("A");
        CheckMaxQue();
        char[] ch = numstr.toCharArray();
        //display ticket numbers
        front1.setText(String.valueOf(ch[0]));
        mid1.setText(String.valueOf(ch[1]));
        last1.setText(String.valueOf(ch[2]));

    }//GEN-LAST:event_catAActionPerformed

    private void catBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catBActionPerformed
    //Button for category B
        String numstr = dispenseTicket("B");
        CheckMaxQue();
        char[] ch = numstr.toCharArray();
        //display ticket numbers
        front2.setText(String.valueOf(ch[0]));
        mid2.setText(String.valueOf(ch[1]));
        last2.setText(String.valueOf(ch[2]));
        //next number button for counter 3

    }//GEN-LAST:event_catBActionPerformed

    private void catCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catCActionPerformed
    //Button for category C
        String numstr = dispenseTicket("C");
        CheckMaxQue();
        char[] ch = numstr.toCharArray();
        //display ticket numbers
        front3.setText(String.valueOf(ch[0]));
        mid3.setText(String.valueOf(ch[1]));
        last3.setText(String.valueOf(ch[2]));
        //next number button for counter 3
    }//GEN-LAST:event_catCActionPerformed

    private void next4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next4ActionPerformed
        //next button for counter 4
        String numstr = callTicket("4");
        CheckMaxQue();
        jLabelC4.setText(numstr); //To display one calling number of counter 4
        jButtonR4.setEnabled(true); //Enable repeat button for the called number
    }//GEN-LAST:event_next4ActionPerformed

    private void next3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next3ActionPerformed
        //next button for counter 3    
        String numstr = callTicket("3");
        CheckMaxQue();
        jLabelC3.setText(numstr); //To display one calling number of counter 3
        jButtonR3.setEnabled(true); //Enable repeat button for the called number
    }//GEN-LAST:event_next3ActionPerformed

    private void next2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next2ActionPerformed
        //next button for counter 2    
        String numstr = callTicket("2");
        CheckMaxQue();
        jLabelC2.setText(numstr);//To display one calling number of counter 2
        jButtonR2.setEnabled(true); //Enable repeat button for the called number
    }//GEN-LAST:event_next2ActionPerformed

    private void jButtonR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonR1ActionPerformed
        //repeat button counter 1
        String numstr = jLabelC1.getText();
        jButtonR1.setEnabled(repeatTicket(numstr, "1"));
    }//GEN-LAST:event_jButtonR1ActionPerformed

    private void next1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_next1ActionPerformed
        //next button counter 1
        String numstr = callTicket("1");
        CheckMaxQue();
        jLabelC1.setText(numstr); //To display one calling number of counter 1
        jButtonR1.setEnabled(true);
    }//GEN-LAST:event_next1ActionPerformed

    private void jButtonR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonR2ActionPerformed
        //Repeat button counter 2
        String numstr = jLabelC2.getText();
        jButtonR2.setEnabled(repeatTicket(numstr, "2"));
    }//GEN-LAST:event_jButtonR2ActionPerformed

    private void jButtonR3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonR3ActionPerformed
        //repeat button counter 3
        String numstr = jLabelC3.getText();
        jButtonR3.setEnabled(repeatTicket(numstr, "3"));
    }//GEN-LAST:event_jButtonR3ActionPerformed

    private void jButtonR4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonR4ActionPerformed
        //repeat button counter 4
        String numstr = jLabelC4.getText();
        jButtonR4.setEnabled(repeatTicket(numstr, "4"));
    }//GEN-LAST:event_jButtonR4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Display log file dialog box
        jTextArea1.setText(logText.toString());
        jDialog1.setTitle("Log");
        jDialog1.setLocation(200, 0); //where it'll appear on screen   
        jDialog1.setSize(900, 750); //size of the GUI
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //Close log file dialog box
        jDialog1.setVisible(false);
        jDialog1.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Frame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Frame1().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton catA;
    private javax.swing.JButton catB;
    private javax.swing.JButton catC;
    private javax.swing.JButton exit;
    private javax.swing.JTextField front1;
    private javax.swing.JTextField front2;
    private javax.swing.JTextField front3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonR1;
    private javax.swing.JButton jButtonR2;
    private javax.swing.JButton jButtonR3;
    private javax.swing.JButton jButtonR4;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelC1;
    private javax.swing.JLabel jLabelC2;
    private javax.swing.JLabel jLabelC3;
    private javax.swing.JLabel jLabelC4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField last1;
    private javax.swing.JTextField last2;
    private javax.swing.JTextField last3;
    private javax.swing.JTextField mid1;
    private javax.swing.JTextField mid2;
    private javax.swing.JTextField mid3;
    private javax.swing.JButton next1;
    private javax.swing.JButton next2;
    private javax.swing.JButton next3;
    private javax.swing.JButton next4;
    // End of variables declaration//GEN-END:variables

}
