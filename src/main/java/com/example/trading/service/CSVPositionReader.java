package com.example.trading.service;

import com.example.trading.model.Position;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVPositionReader {
    public static List<Position> readPositions(String fileName) {
        List<Position> positions = new ArrayList<>();
        try (InputStream is = CSVPositionReader.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String symbol = parts[0].trim();
                    int positionSize = Integer.parseInt(parts[1].trim());
                    positions.add(new Position(symbol, positionSize));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }
}
