<p align="center">
  <img src="https://github.com/user-attachments/assets/4f3d1ba3-ea8e-4ecd-b59d-c1c8a501337c" alt="RepShot Logo" width="680"/>
</p>

# ⚡ RepShot — Security Finding Cards for Burp Suite

> **Turn your Burp Suite findings into clean, professional cards, ready for reports, bug bounty submissions, and social sharing.**

<p align="center">
  <img src="https://img.shields.io/badge/Burp%20Suite-Extension-orange?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Java-17%2B-blue?style=for-the-badge&logo=java"/>
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Burp-Champion-red?style=for-the-badge"/>
</p>

---

## The Problem

Every pentester knows the moment: you've just confirmed a `SQL injection`, an `XSS` payload, or a path traversal returned `/etc/passwd`. Now you need to document it.

The usual workflow looks like this:

1. Open _Flameshot_ or a screenshot tool
2. Take a screenshot of the request
3. Take another screenshot of the response
4. Draw red boxes around the relevant parts manually
5. Open your report template
6. Copy/paste the vulnerability name, write the business impact from scratch
7. Repeat for every single finding

When you're running a pentest or a bug bounty session with 10, 15, or 20 findings - this process kills your momentum. You spend more time documenting than hacking.

**RepShot was built to fix that.**

---

## What RepShot Does ?

RepShot is a Burp Suite extension that adds a **"Send to RepShot"** option to your Repeater context menu. From there, you get a dedicated panel where you can:

- **Scroll to the exact part of the request or response** you want to show
- **Capture that exact viewport** — what you see is what gets exported
- **Draw red annotation boxes** directly on the capture before exporting
- **Search** inside request/response with `Cmd+F` / `Ctrl+F`
- **Auto-fill the business impact** based on the vulnerability type selected
- **Export a professional HD PNG card** ready to paste into any report or post on LinkedIn/X

No more context switching. No more Flameshot. No more writing "An attacker could..." from scratch for the tenth time today.

---

## Screenshots

> *Example finding card exported by RepShot*

<img width="2400" height="1522" alt="repshot-finding2" src="https://github.com/user-attachments/assets/7fda7566-aa37-4624-a8ea-ecb6f72fc720" />

---

## Installation

### Option A - Use the prebuilt JAR (recommended)

1. Download `repshot-1.0.0.jar` from the [Releases](../../releases) page
2. Open Burp Suite
3. Go to **Extensions → Add**
4. Extension type: **Java**
5. Select the downloaded JAR
6. Click **Next** - you should see `RepShot loaded` in the Output tab

**Requirements:** Burp Suite 2023.x or later · Java 17+ on your system

### Option B - Build from source

```bash
git clone https://github.com/JFOZ1010/repshot.git
cd repshot
mvn package
# JAR will be at target/repshot-1.0.0.jar
```

Requirements: Java 17+, Maven 3.8+

---

## How to Use

### Basic workflow

1. Send a request to **Repeater** and fire it
2. **Right-click** anywhere in the request/response → **📸 Send to RepShot**
   
      <img width="602" height="177" alt="image" src="https://github.com/user-attachments/assets/3a2bd081-dce6-4f0d-8916-8d61e9f5d581" />
      
4. The RepShot panel opens with your request and response loaded

   <img width="1166" height="781" alt="image" src="https://github.com/user-attachments/assets/171d7fd0-987d-4604-882c-5fc85a539ae1" />

### Documenting a finding

1. **Fill in the finding details** - title, vulnerability type, severity, your handle
   - Business impact auto-fills based on the vulnerability type selected
   - Selecting a different type updates the impact automatically
   - Choose "Other..." to type a custom vulnerability name
  
     <img width="1059" height="197" alt="image" src="https://github.com/user-attachments/assets/d44b0f44-2ca3-4d29-be0d-74a6d83af480" />
     <img width="622" height="131" alt="image" src="https://github.com/user-attachments/assets/ab04ddf8-438b-43a4-bbf1-2dbc43a835d2" />


2. **Navigate to the relevant part** of the request or response using scroll

3. **Click `[ 📷 Capture ]`** - this captures exactly what's visible in the panel at that moment (_What You See is What You Get_)

4. **Annotate with red boxes** (optional):
   - Click `[ ✏ Draw Box ]` to enter drawing mode
   - Click and drag to draw annotation rectangles over the payload or evidence
     <img width="1010" height="515" alt="image" src="https://github.com/user-attachments/assets/f7f2539b-0d8b-4091-81d1-cabc9427880c" />
   - Click `[ Clear Boxes ]` to remove all boxes
   - Re-capture after drawing to include the boxes in the export
   - Click on `[ ✏ Draw Box ]` again to exit the context of the red box and have the **response scroll**.

5. **Search** with `Cmd+F` (macOS) or `Ctrl+F` (Windows/Linux):
   - Type to find matches in real time, highlighted in yellow
     <img width="566" height="487" alt="image" src="https://github.com/user-attachments/assets/79a7f295-7a53-4165-bbd8-2bf91c3142ab" />
   - Navigate with `‹` and `›` buttons
   - Press `Escape` to close

6. Click **`Preview Card`** to see the result before saving
   
<img width="588" height="185" alt="image" src="https://github.com/user-attachments/assets/3100eb38-df15-4413-929d-70daafc30665" />

7. Click **`Export PNG`** to save the HD card (2400px wide, print-quality)

---

## Vulnerability Types Supported

RepShot includes pre-written business impact templates for 30 vulnerability types:

| Category | Types |
|---|---|
| Injection | SQL Injection (Error-Based, Blind/Boolean, Out-of-Band), Command Injection, SSTI, XXE, GraphQL Injection |
| XSS | Reflected, Stored, DOM-Based |
| Access Control | IDOR, Broken Access Control, Authentication Bypass, Broken Auth / Session Management |
| Server-Side | SSRF, RCE, Path Traversal / LFI, RFI |
| Web Misc | CORS Misconfiguration, HTTP Request Smuggling, Cache Poisoning, Open Redirect, Clickjacking, Subdomain Takeover |
| Client-Side | Prototype Pollution, JWT Vulnerabilities |
| Other | Insecure File Upload, Mass Assignment, Insecure Deserialization, Business Logic Flaw, Other... |

Each template is written in plain business language no jargon, so the impact makes sense to a non-technical audience.

---

## Why RepShot Exists

> *"800 lines of HTML. The evidence is on line 697."*

I was spending too much time on the same repetitive documentation work on every engagement.
The worst part wasn't writing the report, it was this:

RepShot captures exactly what you're looking at in Burp, lets you annotate inline,
and exports a card that works for both technical reports and non-technical stakeholders.

> The same PNG that goes into a pentest report can go on LinkedIn
> without looking like a raw terminal dump.

It also auto-fills the business impact. Because *"An attacker can exploit this SQL injection
to extract the entire database..."* is something I've typed some variation of a hundred times.

---

## Contributing

RepShot is open source and community-driven. If you:

- Found a bug → open an issue
- Want a new vulnerability template → open a PR editing `ImpactTemplates.java`
- Want a new feature → open an issue first to discuss

All contributions welcome.

---

## Built With

- [Burp Suite Montoya API](https://portswigger.net/burp/documentation/desktop/extensions/creating): extension framework
- Java Swing: UI and viewport capture
- Graphics2D: HD PNG rendering
- Maven: build system

---

## License

MIT - use it, fork it, improve it.

---

## `Whoami`

<img src="https://media.licdn.com/dms/image/v2/D4E03AQHRqhzMFLC3sg/profile-displayphoto-scale_400_400/B4EZ4JZ7UCJoAk-/0/1778274253615?e=1781136000&v=beta&t=0hia5hqnxB2aKPZyR3sUQSzwE4P1YiTjFTauy-bPR8I" width="80" align="left" style="border-radius:50%; margin-right:16px"/>

**Juan Felipe Oz** - Application Security Engineer & Security Researcher based in Colombia.

   Software Developer · AppSec Engineer · Security Researcher

[![LinkedIn](https://img.shields.io/badge/LinkedIn-juanfelipeoz-blue?style=flat&logo=linkedin)](https://www.linkedin.com/in/juanfelipeoz/)
[![Web](https://img.shields.io/badge/Web-juanfelipeoz.com-orange?style=flat&logo=firefox)](https://juanfelipeoz.com)
[![X](https://img.shields.io/badge/X-@Pwnedrar__-black?style=flat&logo=x)](https://twitter.com/Pwnedrar_)

---

> *Built with frustration and too many Flameshot screenshots.*
