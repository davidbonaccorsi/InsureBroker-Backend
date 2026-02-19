package com.insurebroker.service;

import com.insurebroker.entity.InsurancePolicy;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {

    public ByteArrayInputStream generatePolicyPdf(InsurancePolicy policy) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, new Color(33, 37, 41));
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(50, 50, 50));
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(80, 80, 80));
            Font highlightFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(0, 102, 204));

            Font signatureFont = FontFactory.getFont(FontFactory.TIMES_ITALIC, 16, new Color(0, 51, 153));

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1f, 1f});

            PdfPCell leftHeader = new PdfPCell();
            leftHeader.setBorder(Rectangle.NO_BORDER);
            leftHeader.addElement(new Paragraph("INSUREBROKER", titleFont));
            leftHeader.addElement(new Paragraph("Secure your future today.", new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));

            PdfPCell rightHeader = new PdfPCell();
            rightHeader.setBorder(Rectangle.NO_BORDER);
            rightHeader.setHorizontalAlignment(Element.ALIGN_RIGHT);
            Paragraph datePara = new Paragraph("Date: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")), normalFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            rightHeader.addElement(datePara);
            Paragraph statusPara = new Paragraph("Status: " + policy.getStatus(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 153, 51)));
            statusPara.setAlignment(Element.ALIGN_RIGHT);
            rightHeader.addElement(statusPara);

            headerTable.addCell(leftHeader);
            headerTable.addCell(rightHeader);
            document.add(headerTable);

            document.add(new Chunk("\n"));
            document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1f, 100f, Color.LIGHT_GRAY, Element.ALIGN_CENTER, -5f)));
            document.add(new Chunk("\n\n"));

            Paragraph docTitle = new Paragraph("OFFICIAL INSURANCE POLICY\nNo. " + policy.getPolicyNumber(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            docTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(docTitle);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 3f});
            table.setSpacingBefore(15f);
            table.setSpacingAfter(20f);

            addTableRow(table, "Client Name", policy.getClientName(), headerFont, normalFont, false);
            addTableRow(table, "Insurance Product", policy.getProductName(), headerFont, normalFont, true);
            addTableRow(table, "Insurer", policy.getInsurerName(), headerFont, normalFont, false);
            addTableRow(table, "Broker in Charge", policy.getBrokerName() + " (" + policy.getBrokerEmail() + ")", headerFont, normalFont, true);

            String startDate = policy.getStartDate() != null ? policy.getStartDate().toString() : "N/A";
            String endDate = policy.getEndDate() != null ? policy.getEndDate().toString() : "N/A";
            addTableRow(table, "Coverage Period", startDate + "  TO  " + endDate, headerFont, normalFont, false);

            addTableRow(table, "Sum Insured", "$" + String.format("%,.2f", policy.getSumInsured()), headerFont, highlightFont, true);
            addTableRow(table, "Total Premium", "$" + String.format("%,.2f", policy.getPremium()), headerFont, highlightFont, false);

            document.add(table);

            document.add(new Paragraph("Terms & Conditions:", headerFont));
            document.add(new Paragraph("1. This policy is governed by the official terms and conditions of " + policy.getInsurerName() + ".", normalFont));
            document.add(new Paragraph("2. The policyholder must promptly notify the broker of any changes to the insured risk.", normalFont));
            document.add(new Paragraph("3. In the event of a claim, contact your broker within 48 hours to initiate procedures.", normalFont));
            document.add(new Paragraph("4. GDPR Consent Status: " + (policy.getGdprConsent() != null && policy.getGdprConsent() ? "Legally Signed & Verified" : "Pending"), normalFont));

            document.add(new Paragraph("\n\n\n\n"));

            PdfPTable signatureTable = new PdfPTable(3);
            signatureTable.setWidthPercentage(100);
            signatureTable.setWidths(new float[]{2f, 1f, 2f});

            PdfPCell brokerCell = new PdfPCell();
            brokerCell.setBorder(Rectangle.NO_BORDER);
            brokerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            brokerCell.addElement(new Paragraph("Broker Authorized Signature\n\n", new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY)));
            Paragraph brokerSignature = new Paragraph(policy.getBrokerName(), signatureFont);
            brokerCell.addElement(brokerSignature);
            brokerCell.addElement(new Paragraph("__________________________", normalFont));
            brokerCell.addElement(new Paragraph("Date: " + java.time.LocalDate.now().toString(), new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY)));

            PdfPCell stampCell = new PdfPCell();
            stampCell.setBorder(Rectangle.NO_BORDER);
            stampCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfContentByte cb = writer.getDirectContent();
            cb.setRGBColorStroke(200, 0, 0);
            cb.setLineWidth(2f);
            cb.circle(297, 160, 35);
            cb.stroke();
            cb.beginText();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED), 10);
            cb.setRGBColorFill(200, 0, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "APPROVED", 297, 165, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "OFFICIAL", 297, 150, 0);
            cb.endText();

            PdfPCell clientCell = new PdfPCell();
            clientCell.setBorder(Rectangle.NO_BORDER);
            clientCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            clientCell.addElement(new Paragraph("Client Acknowledgment\n\n\n\n", new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY)));
            clientCell.addElement(new Paragraph("__________________________", normalFont));
            clientCell.addElement(new Paragraph(policy.getClientName(), new Font(Font.HELVETICA, 10, Font.BOLD, Color.DARK_GRAY)));

            signatureTable.addCell(brokerCell);
            signatureTable.addCell(stampCell);
            signatureTable.addCell(clientCell);

            document.add(signatureTable);

            document.add(new Chunk("\n\n\n\n\n\n"));
            Paragraph footer = new Paragraph("InsureBroker Pro - System Generated Document | UUID: " + java.util.UUID.randomUUID().toString().substring(0, 8), new Font(Font.HELVETICA, 7, Font.ITALIC, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont, boolean isZebra) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(10f);
        labelCell.setBorderColor(new Color(220, 220, 220));

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        valueCell.setPadding(10f);
        valueCell.setBorderColor(new Color(220, 220, 220));

        if (isZebra) {
            Color zebraColor = new Color(248, 249, 250);
            labelCell.setBackgroundColor(zebraColor);
            valueCell.setBackgroundColor(zebraColor);
        }

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}