package com.example.apachepoiexample.service;

import com.example.apachepoiexample.model.Style;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@AllArgsConstructor
public class FileService {

    private final ObjectMapper objectMapper;

    public void readFile(MultipartFile file) {
        try (XWPFDocument doc = new XWPFDocument(
                file.getInputStream())) {
            List<IBodyElement> elements = doc.getBodyElements();
            for (IBodyElement element : elements) {
                BodyElementType beType = element.getElementType();
                if (beType.equals(BodyElementType.PARAGRAPH)) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    System.out.print(paragraph.getText());
                    List<XWPFRun> runs = paragraph.getRuns();
                    if (!runs.isEmpty()) {
                        Style style = getStyle(runs.get(0), doc);
                        style.setType(getStringOrEmpty(paragraph.getStyle()));
                        System.out.print(" " + objectMapper.writeValueAsString(style));
                    }
                    System.out.println();
                } else if (beType.equals(BodyElementType.TABLE)) {
                    XWPFTable xwpfTable = (XWPFTable) element;
                    System.out.println("Total Rows : " + xwpfTable.getNumberOfRows());
                    for (int k = 0; k < xwpfTable.getRows().size(); k++) {
                        System.out.print("row "+(k+1)+": ");
                        for (int j = 0; j < xwpfTable.getRow(k).getTableCells().size(); j++) {
                            String text = getStringOrNull(xwpfTable.getRow(k).getCell(j).getText());
                            System.out.print(text + " | ");
                        }
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Style getStyle(XWPFRun run, XWPFDocument doc) {
        Style style = new Style();
        style.setSize(run.getFontSize() == -1 ? doc.getStyles().getDefaultRunStyle().getFontSize() : run.getFontSize());
        style.setBold(run.isBold());
        style.setItalic(run.isItalic());
        String color = run.getColor() == null ? "default" : getStringOrEmpty(run.getColor());
        style.setColor(color);
        return style;
    }

    private String getStringOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return text;
    }

    private String getStringOrEmpty(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        return text;
    }
}
