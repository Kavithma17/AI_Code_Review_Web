package com.example.analyzers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Configuration;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.LanguageRegistry;

public class JavaAnalyzer {

    public String analyze(File file) {

        StringBuilder report = new StringBuilder();

        report.append("=== JAVA CODE ANALYSIS REPORT ===\n\n");

        // ================= CHECKSTYLE =================
        report.append("=== CHECKSTYLE RESULTS ===\n");
        List<String> checkstyleIssues = runCheckstyle(file);

        if (checkstyleIssues.isEmpty()) {
            report.append("No Checkstyle issues found.\n");
        } else {
            for (String issue : checkstyleIssues) {
                report.append("- ").append(issue).append("\n");
            }
        }

        report.append("\n");

        // ================= PMD =================
        report.append("=== PMD RESULTS ===\n");
        List<String> pmdIssues = runPMD(file);

        if (pmdIssues.isEmpty()) {
            report.append("No PMD issues found.\n");
        } else {
            for (String issue : pmdIssues) {
                report.append("- ").append(issue).append("\n");
            }
        }

        // ================= SCORE =================
        int score = calculateScore(checkstyleIssues, pmdIssues);
        report.append("\nScore: ").append(score).append("/100");

        return report.toString();
    }

    // ================= CHECKSTYLE =================
    private List<String> runCheckstyle(File file) {

        List<String> issues = new ArrayList<>();

        try {
            Checker checker = new Checker();
            checker.setModuleClassLoader(Checker.class.getClassLoader());

            File configFile = new File("checkstyle.xml");

            Configuration config = ConfigurationLoader.loadConfiguration(
                    configFile.getAbsolutePath(),
                    null
            );

            checker.configure(config);

            checker.addListener(new AuditListener() {

                @Override
                public void auditStarted(AuditEvent event) {}

                @Override
                public void auditFinished(AuditEvent event) {}

                @Override
                public void fileStarted(AuditEvent event) {}

                @Override
                public void fileFinished(AuditEvent event) {}

                @Override
                public void addError(AuditEvent event) {
                    issues.add("Line " + event.getLine() + ": " + event.getMessage());
                }

                @Override
                public void addException(AuditEvent event, Throwable throwable) {
                    issues.add("ERROR: " + throwable.getMessage());
                }
            });

            checker.process(List.of(file));
            checker.destroy();

        } catch (Exception e) {
            issues.add("CHECKSTYLE ERROR: " + e.getMessage());
        }

        return issues;
    }

    // ================= PMD =================
    // ================= PMD =================
    @SuppressWarnings("deprecation")
    private List<String> runPMD(File file) {
        List<String> issues = new ArrayList<>();
        
        try {
            PMDConfiguration config = new PMDConfiguration();
config.setDefaultLanguageVersion(
        LanguageRegistry.findLanguageByTerseName("java").getDefaultVersion()
);
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            RuleSets ruleSets = ruleSetFactory.createRuleSets(config.getRuleSets());

            RuleContext ctx = new RuleContext();
            Report report = new Report();
            ctx.setReport(report);
            ctx.setSourceCodeFilename(file.getAbsolutePath());
            
            // Explicitly tell PMD we are analyzing Java code to fix the 'null' error
            ctx.setLanguageVersion(LanguageRegistry.getLanguage("java").getDefaultVersion());

            try (InputStream in = new FileInputStream(file)) {
                SourceCodeProcessor processor = new SourceCodeProcessor(config);
                processor.processSourceCode(in, ruleSets, ctx);
            }

            for (RuleViolation v : report.getViolations()) {
                issues.add("Line " + v.getBeginLine() + ": " + v.getDescription());
            }
        } catch (Exception e) {
            issues.add("PMD Analysis Error: " + e.getMessage());
        }
        return issues;
    }

    // ================= SCORING =================
    private int calculateScore(List<String> checkstyle, List<String> pmd) {

        int issues = checkstyle.size() + pmd.size();

        int score = 100 - (issues * 5);

        return Math.max(score, 0);
    }
}