package com.placement.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.placement.dto.ResumeDTO;
import com.placement.dto.StudentDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {

    public byte[] generateResumePdf(ResumeDTO.ResumeContent content) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD, java.awt.Color.BLACK);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, java.awt.Color.DARK_GRAY);
        Font sectionHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, new java.awt.Color(31, 78, 121));
        Font bodyBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, java.awt.Color.BLACK);
        Font bodyNormal = FontFactory.getFont(FontFactory.HELVETICA, 9.5f, Font.NORMAL, java.awt.Color.BLACK);

        // Header
        if (content.getPersonalInfo() != null) {
            Paragraph name = new Paragraph(content.getPersonalInfo().getFullName() != null ?
                    content.getPersonalInfo().getFullName().toUpperCase() : "STUDENT NAME", nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

            StringBuilder contactLine = new StringBuilder();
            if (content.getPersonalInfo().getEmail() != null) contactLine.append(content.getPersonalInfo().getEmail());
            if (content.getPersonalInfo().getPhone() != null) contactLine.append(" | ").append(content.getPersonalInfo().getPhone());
            if (content.getPersonalInfo().getLocation() != null) contactLine.append(" | ").append(content.getPersonalInfo().getLocation());
            if (content.getPersonalInfo().getLinkedin() != null) contactLine.append(" | ").append(content.getPersonalInfo().getLinkedin());
            if (content.getPersonalInfo().getGithub() != null) contactLine.append(" | ").append(content.getPersonalInfo().getGithub());

            Paragraph contact = new Paragraph(contactLine.toString(), subTitleFont);
            contact.setAlignment(Element.ALIGN_CENTER);
            contact.setSpacingAfter(8);
            document.add(contact);
        }

        LineSeparator separator = new LineSeparator(1, 100, new java.awt.Color(200, 200, 200), Element.ALIGN_CENTER, -2);

        // Professional Summary
        if (content.getSummary() != null && !content.getSummary().isBlank()) {
            addSectionHeader(document, "PROFESSIONAL SUMMARY", sectionHeaderFont, separator);
            Paragraph summary = new Paragraph(content.getSummary(), bodyNormal);
            summary.setSpacingAfter(10);
            document.add(summary);
        }

        // Education
        if (content.getEducation() != null && !content.getEducation().isEmpty()) {
            addSectionHeader(document, "EDUCATION", sectionHeaderFont, separator);
            for (ResumeDTO.EducationItem edu : content.getEducation()) {
                Paragraph eduTitle = new Paragraph(
                        (edu.getDegree() != null ? edu.getDegree() : "") + " - " +
                        (edu.getFieldOfStudy() != null ? edu.getFieldOfStudy() : ""), bodyBold);
                Paragraph eduDetails = new Paragraph(
                        (edu.getInstitution() != null ? edu.getInstitution() : "") +
                        (edu.getGradeOrCgpa() != null ? " | CGPA/Score: " + edu.getGradeOrCgpa() : "") +
                        (edu.getEndYear() != null ? " | Year: " + edu.getEndYear() : ""), bodyNormal);
                eduDetails.setSpacingAfter(6);
                document.add(eduTitle);
                document.add(eduDetails);
            }
        }

        // Technical Skills
        if (content.getSkills() != null && !content.getSkills().isEmpty()) {
            addSectionHeader(document, "TECHNICAL SKILLS", sectionHeaderFont, separator);
            Paragraph skills = new Paragraph(String.join(" • ", content.getSkills()), bodyNormal);
            skills.setSpacingAfter(10);
            document.add(skills);
        }

        // Projects
        if (content.getProjects() != null && !content.getProjects().isEmpty()) {
            addSectionHeader(document, "ACADEMIC & TECHNICAL PROJECTS", sectionHeaderFont, separator);
            for (StudentDTO.ProjectItem proj : content.getProjects()) {
                Paragraph pTitle = new Paragraph(proj.getTitle() +
                        (proj.getTechnologies() != null ? " (" + proj.getTechnologies() + ")" : ""), bodyBold);
                Paragraph pDesc = new Paragraph(proj.getDescription(), bodyNormal);
                pDesc.setSpacingAfter(6);
                document.add(pTitle);
                document.add(pDesc);
            }
        }

        // Experience & Internships
        if (content.getExperiences() != null && !content.getExperiences().isEmpty()) {
            addSectionHeader(document, "EXPERIENCE & INTERNSHIPS", sectionHeaderFont, separator);
            for (StudentDTO.ExperienceItem exp : content.getExperiences()) {
                Paragraph eTitle = new Paragraph(exp.getTitle() + " - " + exp.getOrganization(), bodyBold);
                Paragraph eDesc = new Paragraph(exp.getDescription() != null ? exp.getDescription() : "", bodyNormal);
                eDesc.setSpacingAfter(6);
                document.add(eTitle);
                document.add(eDesc);
            }
        }

        // Certifications
        if (content.getCertifications() != null && !content.getCertifications().isEmpty()) {
            addSectionHeader(document, "CERTIFICATIONS", sectionHeaderFont, separator);
            for (String cert : content.getCertifications()) {
                Paragraph certP = new Paragraph("• " + cert, bodyNormal);
                document.add(certP);
            }
        }

        document.close();
        return out.toByteArray();
    }

    private void addSectionHeader(Document document, String title, Font font, LineSeparator separator) throws DocumentException {
        Paragraph p = new Paragraph(title, font);
        p.setSpacingBefore(8);
        p.setSpacingAfter(2);
        document.add(p);
        document.add(separator);
        document.add(new Paragraph(" "));
    }
}
