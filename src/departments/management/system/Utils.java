/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package departments.management.system;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import departments.management.system.dto.Unit;
import departments.management.system.dto.UnitSection;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

;

/**
 *
 * @author orcl
 */
public class Utils {
    
    public static void main(String[] args) {
        
    }
    
    private static Font catFont;
    private static Font smallFont;
    
    static {
        try {
            BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, true);
            
            catFont = new Font(bf, 14);
            
            smallFont = new Font(bf, 14);
            
        } catch (DocumentException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    static LanguageProcessor al = new ArabicLigaturizer();
    
    static void print(Unit currentlySelectedUnit, List<UnitSection> unitSections) {
        
        try {
            Document document = new Document();
            File file = new File("test.pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            
            document.open();
            PdfPTable table = new PdfPTable(3);
            Paragraph preface = new Paragraph();
            PdfPCell cell1 = new PdfPCell(new Paragraph(al.process(String.format("اسم الوحدة : %s", currentlySelectedUnit.getUnitName())), catFont));
            cell1.setColspan(3);
            cell1.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            
            table.addCell(cell1);
            
            for (UnitSection unitSection : unitSections) {
                
                PdfPCell section = new PdfPCell(new Paragraph(al.process(String.format("قسم : %s", unitSection.getSectionName())), catFont));
                section.setColspan(3);
                section.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                table.addCell(section);
                
                PdfPCell arrearslabel = new PdfPCell(new Paragraph(al.process("المتأخرات"), catFont));
                PdfPCell arrearsvalue = new PdfPCell(new Paragraph(al.process((unitSection.getArrears() != null && unitSection.getArrears().trim().length() > 0) ? unitSection.getArrears() : "لا يوجد"), smallFont));
                arrearsvalue.setColspan(2);
                arrearslabel.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                arrearsvalue.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                table.addCell(arrearsvalue);
                table.addCell(arrearslabel);
                
                PdfPCell actionlabel = new PdfPCell(new Paragraph(al.process("الاجراءات"), catFont));
                PdfPCell actionsvalue = new PdfPCell(new Paragraph(al.process((unitSection.getActions() != null && unitSection.getActions().trim().length() > 0) ? unitSection.getActions() : "لا يوجد"), smallFont));
                actionsvalue.setColspan(2);
                actionlabel.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                actionsvalue.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                table.addCell(actionsvalue);
                table.addCell(actionlabel);
                
                PdfPCell emptyRow = new PdfPCell();
                emptyRow.setColspan(3);
                table.addCell(emptyRow);

//                addEmptyLine(preface, 1);
//
//                preface.add(new Paragraph(al.process("الاجراءات"),
//                        catFont));
//                addEmptyLine(preface, 1);
//                preface.add(new Paragraph(al.process((unitSection.getActions() != null && unitSection.getActions().trim().length() > 0) ? unitSection.getActions() : "لا يوجد"), smallFont));
//
//                addEmptyLine(preface, 3);
            }
            document.add(table);
            document.setHtmlStyleClass("direction:rtl");
            document.close();
            
            Desktop.getDesktop().print(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    
}
