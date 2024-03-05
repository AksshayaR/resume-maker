package com.example.demo;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PredictiveKeyboard implements KeyListener {

    private JTextComponent textComp;
    private JPopupMenu popup;
    private JComboBox<String> suggestionBox;
    private String typedText;
    private String[] dictionary;

    public PredictiveKeyboard(JTextComponent textComp, int wordThreshold) {
        this.textComp = textComp;
        this.popup = new JPopupMenu();
        this.suggestionBox = new JComboBox<String>();
        this.typedText = "";

        // Read the dictionary file and populate the dictionary array
        try (BufferedReader br = new BufferedReader(new FileReader("dictionary.txt"))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            dictionary = lines.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the suggestionBox to the popup
        popup.add(suggestionBox);

        // Set up the suggestionBox to be displayed on a mouse click
        textComp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (suggestionBox.getItemCount() > 0) {
                    popup.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });

        // Add the KeyListener to the text component
        textComp.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Do nothing
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            // Move focus to the suggestionBox
            suggestionBox.requestFocus();
        } else {
            // Get the current text in the text component
            String text = textComp.getText();

            // Find the start and end index of the word being typed
            int startIndex = text.lastIndexOf(" ", textComp.getCaretPosition() - 1) + 1;
            int endIndex = text.indexOf(" ", textComp.getCaretPosition());

            // If the word being typed is the last word in the text, set the endIndex to the end of the text
            if (endIndex == -1) {
                endIndex = text.length();
            }

            // Get the typed text
            typedText = text.substring(startIndex, textComp.getCaretPosition());

            // Clear the suggestionBox
            suggestionBox.removeAllItems();

            // If the typed text is longer than the threshold, show suggestions
            if (typedText.length() >= 3) {
                // Loop through the dictionary and add words that start with the typed text to the suggestionBox
                for (String word : dictionary) {
                    if (word.toLowerCase().startsWith(typedText.toLowerCase())) {
                        suggestionBox.addItem(word);
                    }
                }
            }

            // If there are suggestions, show the popup
            if (suggestionBox.getItemCount() > 0) {
                popup.show(textComp, textComp.getCaret().getMagicCaretPosition().x, textComp.getCaret().getMagicCaretPosition().y + textComp.getFont().getSize());
            } else {
                popup.setVisible(false);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }
}
