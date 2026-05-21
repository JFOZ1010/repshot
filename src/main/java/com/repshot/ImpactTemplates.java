package com.repshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Predefined business impact templates for each vulnerability type.
 * Each template is concise (max 120 chars), non-technical, and business-focused.
 */
public class ImpactTemplates {

    private static final Map<String, String> TEMPLATES = new HashMap<>();

    static {
        // SQL Injection variants
        TEMPLATES.put("SQL Injection (Error-Based)",
            "Attacker can extract sensitive database contents including credentials, PII, and business-critical data.");
        TEMPLATES.put("SQL Injection (Blind/Boolean)",
            "Attacker can systematically extract database contents through time-based or boolean inference techniques.");
        TEMPLATES.put("SQL Injection (Out-of-Band)",
            "Attacker can exfiltrate sensitive data via DNS, HTTP callbacks, or other out-of-band channels.");

        // XSS variants
        TEMPLATES.put("XSS (Reflected)",
            "Attacker can execute malicious scripts in victim browsers via crafted URLs, enabling credential theft and session hijacking.");
        TEMPLATES.put("XSS (Stored)",
            "Attacker can execute malicious code in victim browsers at scale, compromising all users viewing the affected content.");
        TEMPLATES.put("XSS (DOM-Based)",
            "Attacker can exploit client-side JavaScript to execute malicious code, compromising user sessions and data.");

        // Injection & Command Execution
        TEMPLATES.put("Command Injection",
            "Attacker can execute arbitrary system commands on the server, potentially gaining full system compromise.");
        TEMPLATES.put("Remote Code Execution (RCE)",
            "Attacker can execute arbitrary code on the server, leading to complete infrastructure compromise and data breach.");
        TEMPLATES.put("Server-Side Template Injection (SSTI)",
            "Attacker can inject malicious template code to achieve RCE, file read, or sensitive data exposure.");

        // Path & File Access
        TEMPLATES.put("Path Traversal / LFI",
            "Attacker can read arbitrary files from the server, potentially accessing source code, configs, and system files.");
        TEMPLATES.put("Remote File Inclusion (RFI)",
            "Attacker can include and execute remote files, potentially leading to RCE, malware injection, or data theft.");

        // XXE & SSRF
        TEMPLATES.put("XML External Entity (XXE)",
            "Attacker can read local files, perform SSRF attacks, or trigger denial-of-service through XML parsing.");
        TEMPLATES.put("Server-Side Request Forgery (SSRF)",
            "Attacker can access internal systems and cloud metadata, potentially compromising the entire infrastructure.");

        // Access Control
        TEMPLATES.put("Insecure Direct Object Reference (IDOR)",
            "Attacker can access unauthorized user data by manipulating object references, causing widespread privacy breach.");
        TEMPLATES.put("Broken Access Control",
            "Attacker can bypass authorization checks to access restricted functions, data, or admin panels.");

        // File & Upload
        TEMPLATES.put("Insecure File Upload",
            "Attacker can upload malicious files to execute code, overwrite system files, or bypass security controls.");

        // Redirect & Cache
        TEMPLATES.put("Open Redirect",
            "Attacker can redirect users to malicious sites via trusted domain, enabling phishing and credential theft at scale.");
        TEMPLATES.put("Cache Poisoning",
            "Attacker can inject malicious content into caches affecting all subsequent users, causing widespread compromise.");

        // Protocol & Messaging
        TEMPLATES.put("HTTP Request Smuggling",
            "Attacker can bypass security controls by exploiting HTTP parsing differences, leading to request desynchronization attacks.");
        TEMPLATES.put("CORS Misconfiguration",
            "Attacker can bypass CORS protections to perform unauthorized cross-origin requests, stealing user data and sessions.");

        // UI & Social
        TEMPLATES.put("Clickjacking",
            "Attacker can trick users into clicking hidden malicious elements, leading to unauthorized actions or data theft.");
        TEMPLATES.put("Subdomain Takeover",
            "Attacker can take control of subdomains via expired DNS records or registrations, potentially phishing users and stealing data.");

        // Authentication & Sessions
        TEMPLATES.put("Authentication Bypass",
            "Attacker can bypass authentication mechanisms entirely, gaining unauthorized access to user accounts and sensitive data.");
        TEMPLATES.put("Broken Auth / Session Management",
            "Attacker can hijack or manipulate user sessions, impersonating legitimate users and accessing their data and functions.");
        TEMPLATES.put("JWT Vulnerabilities",
            "Attacker can forge, tamper with, or crack JWT tokens to bypass authentication and gain unauthorized access.");

        // Advanced Injection
        TEMPLATES.put("Mass Assignment",
            "Attacker can modify unintended object properties during requests, potentially escalating privileges or modifying business logic.");
        TEMPLATES.put("GraphQL Injection",
            "Attacker can exploit GraphQL queries to access unauthorized data, bypass authentication, or perform injection attacks.");
        TEMPLATES.put("Prototype Pollution",
            "Attacker can pollute JavaScript prototype chain to modify object behavior, leading to bypasses and code execution.");
        TEMPLATES.put("Insecure Deserialization",
            "Attacker can execute arbitrary code through malicious serialized objects, leading to complete application compromise.");

        // Business Logic
        TEMPLATES.put("Business Logic Flaw",
            "Attacker can exploit application logic flaws to bypass controls, commit fraud, or cause unauthorized business actions.");

        // Other (empty template)
        TEMPLATES.put("Other...", "");
    }

    /**
     * Get the business impact template for a given vulnerability type.
     * @param vulnType The vulnerability type
     * @return The template string, or empty string if not found or "Other..."
     */
    public static String get(String vulnType) {
        return TEMPLATES.getOrDefault(vulnType, "");
    }
}
