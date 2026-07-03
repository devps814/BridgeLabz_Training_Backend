package com.greet.servlet;

import java.io.IOException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.greet.model.Greeting;
import com.greet.service.GreetingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Front controller servlet that handles greeting requests.
 *
 * Flow:
 * 1. init() → Loads Spring ApplicationContext from applicationContext.xml
 * 2. doGet() → Forwards to index.jsp (shows the form)
 * 3. doPost() → Reads "userName" parameter → calls GreetingService → forwards to greeting.jsp
 *
 * Mapped to URL pattern "/greet" in web.xml.
 */
public class GreetingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    /** Spring Application Context — holds all beans */
    private ApplicationContext context;
    /** The greeting service bean — retrieved from Spring context */
    private GreetingService greetingService;
    // ───────────────────────── Servlet Lifecycle ─────────────────────────
    /**
     * Called once when the servlet is first loaded by Tomcat.
     * Initializes the Spring ApplicationContext and retrieves the GreetingService bean.
     *
     * The applicationContext.xml file must be on the classpath.
     * For Eclipse: place it in WebContent/WEB-INF/ and it's accessible via
     * /WEB-INF/applicationContext.xml
     *
     * Note: We use ClassPathXmlApplicationContext, so the XML must be
     * accessible from the classpath. Alternatively, you can place
     * the XML inside src/ or use a full path.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("=== GreetingServlet.init() — Loading Spring Context ===");
        // Load Spring context from the XML file on the classpath
        // The file is located at WEB-INF/applicationContext.xml
        // We use the servlet context to get the real path
        String xmlPath = getServletContext().getRealPath("/WEB-INF/applicationContext.xml");
        context = new ClassPathXmlApplicationContext("file:" + xmlPath);
        // Retrieve the GreetingService bean from Spring context
        greetingService = (GreetingService) context.getBean("greetingService");
        System.out.println("=== Spring Context loaded successfully ===");
        System.out.println(" GreetingService bean: " + greetingService);
    }
    // ───────────────────────── HTTP GET ─────────────────────────
    /**
     * Handles GET requests — forwards to index.jsp to show the form.
     *
     * URL: http://localhost:8080/GreetingWebApp/greet
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== GreetingServlet.doGet() — Forwarding to index.jsp ===");
        // Forward to the input form page
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
    // ───────────────────────── HTTP POST ─────────────────────────
    /**
     * Handles POST requests — processes the greeting form submission.
     *
     * Steps:
     * 1. Read the "userName" parameter from the form
     * 2. Call greetingService.greet(userName) to get a Greeting object
     * 3. Set the Greeting object as a request attribute
     * 4. Forward to greeting.jsp to display the result
     spring-greeting-webapp-plan-solution.md 2026-06-21
     13 / 23
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Read the "userName" parameter from the submitted form
        String userName = request.getParameter("userName");
        System.out.println("=== GreetingServlet.doPost() — userName: " + userName + " ===");
        // 2. Call the Spring-managed GreetingService to generate the greeting
        Greeting greeting = greetingService.greet(userName);
        System.out.println(" Greeting generated: " + greeting);
        // 3. Set the Greeting object as a request attribute for the JSP
        request.setAttribute("greeting", greeting);
        // 4. Forward to greeting.jsp to display the result
        request.getRequestDispatcher("/greeting.jsp").forward(request, response);
    }
}
