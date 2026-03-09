import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class DesktopMusicPlayer {
    Clip clip;
    JFrame frame;
    JSlider volumeSlider;
    JComboBox<String> songlist;

    public DesktopMusicPlayer() {

        frame = new JFrame("BK Music Player");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // --- Song List ---
        songlist = new JComboBox<>();

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(new JLabel("Select Song:", SwingConstants.CENTER));
        top.add(songlist);
        frame.add(top, BorderLayout.NORTH);

        // --- Volume Slider ---
        volumeSlider = new JSlider(0, 100, 75);
        volumeSlider.addChangeListener(e -> {
            setVolume(volumeSlider.getValue() / 100f);
        });

        JPanel volumePanel = new JPanel();
        volumePanel.add(new JLabel("Volume:"));
        volumePanel.add(volumeSlider);
        frame.add(volumePanel, BorderLayout.CENTER);

        // --- Buttons ---
        JButton addSong = new JButton("Add Song");
        JButton play = new JButton("Play");
        JButton stop = new JButton("Stop");
        JButton reset = new JButton("Reset");
        JButton close = new JButton("Close");

        JPanel bottom = new JPanel(new GridLayout(1, 5));
        bottom.add(addSong);
        bottom.add(play);
        bottom.add(stop);
        bottom.add(reset);
        bottom.add(close);
        frame.add(bottom, BorderLayout.SOUTH);

        // --- Add Song Button (JFileChooser) ---
        addSong.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("WAV Files", "wav");
            chooser.setFileFilter(filter);

            int result = chooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                songlist.addItem(selectedFile.getAbsolutePath());
                songlist.setSelectedItem(selectedFile.getAbsolutePath());
            }
        });

        // --- When Song Selected ---
        songlist.addActionListener(e -> {
            String selectedSong = (String) songlist.getSelectedItem();
            if (selectedSong != null) {
                uploadSong(selectedSong);
            }
        });

        // --- Play ---
        play.addActionListener(e -> {
            if (clip != null) clip.start();
        });

        // --- Stop ---
        stop.addActionListener(e -> {
            if (clip != null) clip.stop();
        });

        // --- Reset ---
        reset.addActionListener(e -> {
            if (clip != null) clip.setMicrosecondPosition(0);
        });

        // --- Close ---
        close.addActionListener(e -> System.exit(0));

        frame.setVisible(true);
    }

    private void uploadSong(String filepath) {
        try {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }

            File file = new File(filepath);
            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(file);

            clip = AudioSystem.getClip();
            clip.open(audioStream);

            setVolume(volumeSlider.getValue() / 100f);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error loading music:\n" + e.getMessage());
        }
    }

    private void setVolume(float volume) {
        if (clip != null &&
                clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {

            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume)
                    / Math.log(10.0) * 20.0);

            gainControl.setValue(dB);
        }
    }

    public static void main(String[] args) {
        new DesktopMusicPlayer();
    }
}


