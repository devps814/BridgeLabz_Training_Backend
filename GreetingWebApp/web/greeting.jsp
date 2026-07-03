<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Greeting Result</title>
    <style>
        /* ═══════════════════════════════════════════════════════════
        Page Styles — Matches index.jsp theme
        ═══════════════════════════════════════════════════════════ */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container {
            background: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            text-align: center;
            max-width: 450px;
            width: 90%;
        }
        .emoji {
            font-size: 64px;
            margin-bottom: 20px;
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 24px;
        }
        .greeting-message {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px 30px;
            border-radius: 10px;
            font-size: 22px;
            font-weight: 600;
            margin: 20px 0;
            word-wrap: break-word;
        }
        a {
            display: inline-block;
            margin-top: 20px;
            padding: 12px 30px;
            background: #f0f0f0;
            color: #555;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
            transition: background 0.3s;
        }
        a:hover {
            background: #e0e0e0;
        }
        .footer {
            margin-top: 20px;
            color: #999;
            font-size: 12px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="emoji">👋</div>
    <h1>Your Greeting</h1>
    <!--
 ${greeting.message} reads the 'message' property from the
 Greeting object that was set as a request attribute in
 GreetingServlet.doPost():
 request.setAttribute("greeting", greetingObj);
 JSP EL (Expression Language) calls greeting.getMessage()
 -->
    <div class="greeting-message">
        ${greeting.message}
    </div>
    <!-- Link back to the form -->
    <a href="index.jsp">← Try Another Name</a>
    <div class="footer">
        Powered by Spring Framework 6.1.6 | Tomcat 10.1
    </div>
</div>
</body>
</html>
